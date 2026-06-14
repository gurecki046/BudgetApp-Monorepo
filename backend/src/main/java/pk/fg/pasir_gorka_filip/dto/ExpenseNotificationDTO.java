package pk.fg.pasir_gorka_filip.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExpenseNotificationDTO {
    private String type;
    private Long groupId;
    private String groupName;
    private String title;
    private Double amount;
    private Double userShare;
    private String createdByEmail;
    private String message;
}
