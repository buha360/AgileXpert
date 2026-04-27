package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for creating a new submenu")
public class CreateSubMenuRequest {

    @Schema(description = "Name of the submenu", example = "Games", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;
}