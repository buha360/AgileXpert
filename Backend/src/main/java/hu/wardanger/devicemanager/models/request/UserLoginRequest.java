package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for authenticating a user")
public class UserLoginRequest {

    @Schema(description = "User password", example = "asd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}