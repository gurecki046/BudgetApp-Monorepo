package pk.fg.pasir_gorka_filip.controller;
import jakarta.validation.Valid;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import pk.fg.pasir_gorka_filip.dto.GroupResponseDTO;
import pk.fg.pasir_gorka_filip.dto.MembershipDTO;
import pk.fg.pasir_gorka_filip.dto.MembershipResponseDTO;
import pk.fg.pasir_gorka_filip.model.Membership;
import pk.fg.pasir_gorka_filip.model.User;
import pk.fg.pasir_gorka_filip.repository.GroupRepository;
import pk.fg.pasir_gorka_filip.service.CurrentUserService;
import pk.fg.pasir_gorka_filip.service.MembershipService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MembershipGraphQLController {

    private final MembershipService membershipService;
    private final GroupRepository groupRepository;
    private final CurrentUserService currentUserService;

    public MembershipGraphQLController(
            MembershipService membershipService,
            GroupRepository groupRepository,
            CurrentUserService currentUserService) {
        this.membershipService = membershipService;
        this.groupRepository = groupRepository;
        this.currentUserService = currentUserService;
    }

    @QueryMapping
    public List<MembershipResponseDTO> groupMembers(@Argument Long groupId) {
        return membershipService.getGroupMembers(groupId).stream()
                .map(membership -> new MembershipResponseDTO(
                        membership.getId(),
                        membership.getUser().getId(),
                        membership.getGroup().getId(),
                        membership.getUser().getEmail()
                ))
                .collect(Collectors.toList());
    }

    @MutationMapping
    public MembershipResponseDTO addMember(@Valid @Argument MembershipDTO membershipDTO) {
        Membership membership = membershipService.addMember(membershipDTO);
        return new MembershipResponseDTO(
                membership.getId(),
                membership.getUser().getId(),
                membership.getGroup().getId(),
                membership.getUser().getEmail()
        );
    }

    @QueryMapping
    public List<GroupResponseDTO> myGroups() {
        User currentUser = currentUserService.getCurrentUser();
        return groupRepository.findByMemberships_User(currentUser).stream()
                .map(group -> new GroupResponseDTO(
                        group.getId(),
                        group.getName(),
                        group.getOwner().getId()
                ))
                .collect(Collectors.toList());
    }

    @MutationMapping
    public Boolean removeMember(@Argument Long membershipId) {
        membershipService.removeMember(membershipId);
        return true;
    }
}