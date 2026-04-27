package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents an available theme")
public class ThemeResponse {

    @Schema(description = "Unique identifier of the theme", example = "theme-dark")
    private String id;

    @Schema(description = "Name of the theme", example = "Dark")
    private String name;
}