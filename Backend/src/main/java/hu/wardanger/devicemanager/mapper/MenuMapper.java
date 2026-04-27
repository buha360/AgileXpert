package hu.wardanger.devicemanager.mapper;

import hu.wardanger.devicemanager.models.response.MenuItemResponse;
import hu.wardanger.devicemanager.models.response.SubMenuResponse;
import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuMapper {
    MenuItemResponse toMenuItemResponse(MenuItem menuItem);

    List<MenuItemResponse> toMenuItemResponseList(List<MenuItem> menuItems);

    SubMenuResponse toSubMenuResponse(Menu menu);

    List<SubMenuResponse> toSubMenuResponseList(List<Menu> menus);
}