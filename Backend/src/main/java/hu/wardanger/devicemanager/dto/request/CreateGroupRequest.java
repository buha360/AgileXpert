package hu.wardanger.devicemanager.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateGroupRequest {
    private String groupName;
    private String accessCode;
    private String adminUserName;
    private String adminPassword;
}