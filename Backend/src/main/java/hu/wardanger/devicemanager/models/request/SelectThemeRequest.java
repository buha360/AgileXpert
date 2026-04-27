package hu.wardanger.devicemanager.models.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Request body for selecting a theme for a user")
public class SelectThemeRequest {

    @Schema(description = "Identifier of the selected theme", example = "theme-dark", requiredMode = Schema.RequiredMode.REQUIRED)
    private String themeId;
}