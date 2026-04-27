package hu.wardanger.devicemanager.models.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Result of validating a group's access code")
public class GroupAccessValidationResponse {

    @Schema(description = "Whether the provided access code is valid", example = "true")
    private boolean valid;

    @Schema(description = "Validation result message", example = "Az access code helyes.")
    private String message;
}