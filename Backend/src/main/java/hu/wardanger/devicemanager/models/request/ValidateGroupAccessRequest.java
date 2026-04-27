package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for validating a group's access code")
public class ValidateGroupAccessRequest {

    @Schema(description = "Access code of the group", example = "asd", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessCode;
}