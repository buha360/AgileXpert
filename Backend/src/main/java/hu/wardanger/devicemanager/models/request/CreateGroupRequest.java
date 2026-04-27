package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for creating a new group with its initial admin user")
public class CreateGroupRequest {

    @Schema(description = "Name of the group", example = "Buha Family", requiredMode = Schema.RequiredMode.REQUIRED)
    private String groupName;

    @Schema(description = "Access code used to enter the group", example = "asd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessCode;

    @Schema(description = "Name of the initial admin user", example = "Buha", requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminUserName;

    @Schema(description = "Password of the initial admin user", example = "asd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String adminPassword;
}