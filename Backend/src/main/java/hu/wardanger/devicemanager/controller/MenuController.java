package hu.wardanger.devicemanager.controller;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.generated.api.MenusApi;
import hu.wardanger.devicemanager.generated.model.AddApplicationRequest;
import hu.wardanger.devicemanager.generated.model.CreateSubMenuRequest;
import hu.wardanger.devicemanager.generated.model.LaunchResponse;
import hu.wardanger.devicemanager.generated.model.RootMenuResponse;
import hu.wardanger.devicemanager.generated.model.SubMenuResponse;
import hu.wardanger.devicemanager.mapper.MenuMapper;
import hu.wardanger.devicemanager.service.MenuManagementService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Data
public class MenuController implements MenusApi {

    private final MenuManagementService menuManagementService;
    private final MenuMapper menuMapper;

    @Override
    public ResponseEntity<RootMenuResponse> getRootMenu(String userId) {
        UserAccount user = menuManagementService.findUserWithRootMenuItems(userId);
        Menu rootMenu = user.getRootMenu();

        List<MenuItem> sortedMenuItems = getSortedMenuItems(rootMenu);

        List<Menu> subMenus = rootMenu.getChildMenus() == null
                ? List.of()
                : rootMenu.getChildMenus();

        RootMenuResponse response = new RootMenuResponse();
        response.setMenuName(rootMenu.getName());
        response.setWallpaperName(
                JsonNullable.of(user.getWallpaper() == null ? null : user.getWallpaper().getName())
        );
        response.setThemeName(
                JsonNullable.of(user.getTheme() == null ? null : user.getTheme().getName())
        );
        response.setApplications(menuMapper.toMenuItemResponseList(sortedMenuItems));
        response.setSubMenus(menuMapper.toSubMenuResponseList(subMenus));

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> addApplicationToRootMenu(String userId, AddApplicationRequest request) {
        menuManagementService.addApplicationToRootMenu(userId, request.getApplicationId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public ResponseEntity<Void> removeApplicationFromRootMenu(String userId, String menuItemId) {
        menuManagementService.removeApplicationFromRootMenu(userId, menuItemId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LaunchResponse> launchApplicationFromRootMenu(String userId, String menuItemId) {
        String launchMessage = menuManagementService.launchApplicationFromRootMenu(userId, menuItemId);

        LaunchResponse response = new LaunchResponse();
        response.setMessage(launchMessage);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<SubMenuResponse>> getSubMenus(String userId) {
        List<Menu> subMenus = menuManagementService.findSubMenus(userId);

        List<SubMenuResponse> response = menuMapper.toSubMenuResponseList(subMenus);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> createSubMenu(String userId, CreateSubMenuRequest request) {
        menuManagementService.createSubMenu(userId, request.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public ResponseEntity<Void> deleteSubMenu(String userId, String submenuId) {
        menuManagementService.deleteSubMenu(userId, submenuId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<RootMenuResponse> getSubMenu(String submenuId) {
        Menu submenu = menuManagementService.findSubMenuWithItems(submenuId);

        List<MenuItem> sortedMenuItems = getSortedMenuItems(submenu);

        RootMenuResponse response = new RootMenuResponse();
        response.setMenuName(submenu.getName());
        response.setWallpaperName(JsonNullable.of(null));
        response.setThemeName(JsonNullable.of(null));
        response.setApplications(menuMapper.toMenuItemResponseList(sortedMenuItems));
        response.setSubMenus(List.of());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> addApplicationToSubMenu(String submenuId, AddApplicationRequest request) {
        menuManagementService.addApplicationToSubMenu(submenuId, request.getApplicationId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .build();
    }

    @Override
    public ResponseEntity<Void> removeApplicationFromSubMenu(String submenuId, String menuItemId) {
        menuManagementService.removeApplicationFromSubMenu(submenuId, menuItemId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LaunchResponse> launchSubMenuApplication(String submenuId, String menuItemId) {
        String launchMessage = menuManagementService.launchApplicationFromSubMenu(submenuId, menuItemId);

        LaunchResponse response = new LaunchResponse();
        response.setMessage(launchMessage);

        return ResponseEntity.ok(response);
    }

    private List<MenuItem> getSortedMenuItems(Menu menu) {
        if (menu.getMenuItems() == null) {
            return List.of();
        }

        return menu.getMenuItems()
                .stream()
                .sorted(Comparator.comparingInt(item ->
                        item.getPositionIndex() == null ? Integer.MAX_VALUE : item.getPositionIndex()))
                .toList();
    }
}