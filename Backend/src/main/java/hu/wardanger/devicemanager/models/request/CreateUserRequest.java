package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for creating a new user inside a group")
public class CreateUserRequest {

    @Schema(description = "Name of the new user", example = "Niki", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @Schema(description = "Password of the new user", example = "asd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}