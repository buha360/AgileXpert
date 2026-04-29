package hu.wardanger.devicemanager.mapper;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.generated.model.MenuItemResponse;
import hu.wardanger.devicemanager.generated.model.SubMenuResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MenuMapper {

    MenuItemResponse toMenuItemResponse(MenuItem menuItem);

    List<MenuItemResponse> toMenuItemResponseList(List<MenuItem> menuItems);

    SubMenuResponse toSubMenuResponse(Menu menu);

    List<SubMenuResponse> toSubMenuResponseList(List<Menu> menus);
}