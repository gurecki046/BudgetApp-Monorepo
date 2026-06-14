package pk.fg.pasir_gorka_filip.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import pk.fg.pasir_gorka_filip.config.MyWebSocketHandler;
import pk.fg.pasir_gorka_filip.dto.ExpenseNotificationDTO;
import pk.fg.pasir_gorka_filip.dto.GroupTransactionDTO;
import pk.fg.pasir_gorka_filip.model.Debt;
import pk.fg.pasir_gorka_filip.model.Group;
import pk.fg.pasir_gorka_filip.model.Membership;
import pk.fg.pasir_gorka_filip.model.User;
import pk.fg.pasir_gorka_filip.repository.DebtRepository;
import pk.fg.pasir_gorka_filip.repository.GroupRepository;
import pk.fg.pasir_gorka_filip.repository.MembershipRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class GroupTransactionService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private MembershipRepository membershipRepository;

    @Autowired
    private DebtRepository debtRepository;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MyWebSocketHandler webSocketHandler;

    public void addGroupTransaction(GroupTransactionDTO transactionDTO, User currentUser) {
        Group group = groupRepository.findById(transactionDTO.getGroupId())
                .orElseThrow(() -> new EntityNotFoundException("Nie znaleziono Grupy"));

        membershipService.assertCurrentUserIsGroupMember(group.getId());

        List<Membership> members = membershipRepository.findByGroupId(group.getId());
        List<Membership> selectedMembers = selectParticipants(transactionDTO, members, currentUser);

        if (selectedMembers.isEmpty()) {
            throw new IllegalStateException("Grupa nie ma czlonkow, nie mozna dodac transakcji.");
        }

        double amountPerUser = transactionDTO.getAmount() / selectedMembers.size();
        boolean expense = "EXPENSE".equals(transactionDTO.getType());

        for (Membership member : selectedMembers) {
            User otherUser = member.getUser();
            if (!otherUser.getId().equals(currentUser.getId())) {

                Debt debt = new Debt();
                debt.setDebtor(expense ? otherUser : currentUser);
                debt.setCreditor(expense ? currentUser : otherUser);
                debt.setGroup(group);
                debt.setAmount(amountPerUser);
                debt.setTitle(transactionDTO.getTitle());
                debtRepository.save(debt);

                // Powiadomienia WebSocket
                String msgText = String.format("%s dodał wydatek \"%s\" w grupie %s. Twoja część: %.2f zł.",
                        currentUser.getEmail(), transactionDTO.getTitle(), group.getName(), amountPerUser);

                ExpenseNotificationDTO notification = ExpenseNotificationDTO.builder()
                        .type("GROUP_EXPENSE_ADDED")
                        .groupId(group.getId())
                        .groupName(group.getName())
                        .title(transactionDTO.getTitle())
                        .amount(transactionDTO.getAmount())
                        .userShare(amountPerUser)
                        .createdByEmail(currentUser.getEmail())
                        .message(msgText)
                        .build();

                webSocketHandler.sendNotification(otherUser.getEmail(), notification);
            }
        }
    }

    private List<Membership> selectParticipants(
            GroupTransactionDTO transactionDTO,
            List<Membership> members,
            User currentUser) {

        List<Long> selectedUserIds = transactionDTO.getSelectedUserIds();

        if (selectedUserIds == null || selectedUserIds.isEmpty()) {
            return members;
        }

        Set<Long> uniqueSelectedUserIds = new HashSet<>(selectedUserIds);
        List<Membership> selectedMembers = members.stream()
                .filter(membership -> uniqueSelectedUserIds.contains(membership.getUser().getId()))
                .toList();

        if (selectedMembers.size() != uniqueSelectedUserIds.size()) {
            throw new IllegalStateException("Wszyscy wybrani uzytkownicy musza byc członkami grupy.");
        }

        boolean currentUserSelected = selectedMembers.stream()
                .anyMatch(membership -> membership.getUser().getId().equals(currentUser.getId()));

        if (!currentUserSelected) {
            throw new IllegalStateException("Aktualny uzytkownik musi byc uczestnikiem transakcji grupowej.");
        }

        if (selectedMembers.size() < 2) {
            throw new IllegalStateException("Transakcja grupowa wymaga co najmniej dwoch uczestnikow.");
        }

        return selectedMembers;
    }
}