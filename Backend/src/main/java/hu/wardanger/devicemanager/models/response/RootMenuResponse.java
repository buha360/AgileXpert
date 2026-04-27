package hu.wardanger.devicemanager.models.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Detailed representation of a user's root menu")
public class RootMenuResponse {

    @Schema(description = "Name of the root menu", example = "Buha főmenüje")
    private String menuName;

    @Schema(description = "Currently selected wallpaper name", example = "Mountain")
    private String wallpaperName;

    @Schema(description = "Currently selected theme name", example = "Dark")
    private String themeName;

    @Schema(description = "Applications displayed directly in the root menu")
    private List<MenuItemResponse> applications;

    @Schema(description = "Submenus accessible from the root menu")
    private List<SubMenuResponse> subMenus;
}