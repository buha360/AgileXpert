package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Short summary of a submenu")
public class SubMenuResponse {

    @Schema(description = "Unique identifier of the submenu", example = "submenu-games")
    private String id;

    @Schema(description = "Name of the submenu", example = "Games")
    private String name;
}