package hu.wardanger.devicemanager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RootMenuResponse {
    private String menuName;
    private String wallpaperName;
    private String themeName;
    private List<MenuItemResponse> applications;
    private List<SubMenuResponse> subMenus;
}