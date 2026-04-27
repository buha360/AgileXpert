package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for adding an application to a menu or submenu")
public class AddApplicationRequest {

    @Schema(description = "Identifier of the application to add", example = "app-openmap", requiredMode = Schema.RequiredMode.REQUIRED)
    private String applicationId;
}