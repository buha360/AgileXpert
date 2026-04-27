package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents an application item displayed in a menu")
public class MenuItemResponse {

    @Schema(description = "Unique identifier of the menu item", example = "menu-item-1")
    private String id;

    @Schema(description = "Display name of the menu item", example = "Paint")
    private String name;

    @Schema(description = "Position index of the item inside the menu", example = "1")
    private Integer positionIndex;
}