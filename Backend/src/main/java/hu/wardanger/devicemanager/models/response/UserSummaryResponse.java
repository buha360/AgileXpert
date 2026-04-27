package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Short summary of a user")
public class UserSummaryResponse {

    @Schema(description = "Unique identifier of the user", example = "user-buha-admin")
    private String id;

    @Schema(description = "Name of the user", example = "Buha")
    private String name;

    @Schema(description = "Role of the user", example = "ADMIN")
    private String role;
}