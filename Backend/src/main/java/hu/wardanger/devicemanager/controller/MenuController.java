package hu.wardanger.devicemanager.controller;

import hu.wardanger.devicemanager.models.request.AddApplicationRequest;
import hu.wardanger.devicemanager.models.request.CreateSubMenuRequest;
import hu.wardanger.devicemanager.models.response.LaunchResponse;
import hu.wardanger.devicemanager.models.response.RootMenuResponse;
import hu.wardanger.devicemanager.models.response.SubMenuResponse;
import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.mapper.MenuMapper;
import hu.wardanger.devicemanager.service.MenuManagementService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;
import java.util.List;

@Tag(name = "Menus", description = "Endpoints for managing root menus, submenus and applications")
@RestController
@Data
@AllArgsConstructor
public class MenuController {

    private final MenuManagementService menuManagementService;
    private final MenuMapper menuMapper;

    @Operation(
            summary = "Get user's root menu",
            description = "Returns the root menu of the selected user, including applications, submenus, wallpaper and theme."
    )
    @ApiResponse(responseCode = "200", description = "Root menu returned successfully")
    @GetMapping("/api/users/{userId}/menu")
    public RootMenuResponse getRootMenu(@PathVariable String userId) {
        UserAccount user = menuManagementService.findUserWithRootMenuItems(userId);
        Menu rootMenu = user.getRootMenu();

        List<MenuItem> sortedMenuItems = rootMenu.getMenuItems() == null
                ? List.of()
                : rootMenu.getMenuItems().stream()
                .sorted(Comparator.comparingInt(item ->
                        item.getPositionIndex() == null ? Integer.MAX_VALUE : item.getPositionIndex()))
                .toList();

        List<Menu> subMenus = rootMenu.getChildMenus() == null
                ? List.of()
                : rootMenu.getChildMenus();

        return new RootMenuResponse(
                rootMenu.getName(),
                user.getWallpaper().getName(),
                user.getTheme().getName(),
                menuMapper.toMenuItemResponseList(sortedMenuItems),
                menuMapper.toSubMenuResponseList(subMenus)
        );
    }

    @Operation(
            summary = "Add application to root menu",
            description = "Adds an application to the selected user's root menu."
    )
    @ApiResponse(responseCode = "201", description = "Application added successfully")
    @PostMapping("/api/users/{userId}/menu/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public void addApplicationToRootMenu(@PathVariable String userId,
                                         @RequestBody AddApplicationRequest request) {
        menuManagementService.addApplicationToRootMenu(userId, request.getApplicationId());
    }

    @Operation(
            summary = "Remove application from root menu",
            description = "Removes an application from the selected user's root menu."
    )
    @ApiResponse(responseCode = "204", description = "Application removed successfully")
    @DeleteMapping("/api/users/{userId}/menu/applications/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeApplicationFromRootMenu(@PathVariable String userId,
                                              @PathVariable String menuItemId) {
        menuManagementService.removeApplicationFromRootMenu(userId, menuItemId);
    }

    @Operation(
            summary = "Launch application from root menu",
            description = "Launches the selected application from the user's root menu."
    )
    @ApiResponse(responseCode = "200", description = "Application launched successfully")
    @PostMapping("/api/users/{userId}/menu/applications/{menuItemId}/launch")
    public LaunchResponse launchApplicationFromRootMenu(@PathVariable String userId,
                                                        @PathVariable String menuItemId) {
        String launchMessage = menuManagementService.launchApplicationFromRootMenu(userId, menuItemId);
        return new LaunchResponse(launchMessage);
    }

    @Operation(
            summary = "List user's submenus",
            description = "Returns all submenus of the selected user."
    )
    @ApiResponse(responseCode = "200", description = "Submenus returned successfully")
    @GetMapping("/api/users/{userId}/submenus")
    public List<SubMenuResponse> getSubMenus(@PathVariable String userId) {
        List<Menu> subMenus = menuManagementService.findSubMenus(userId);
        return menuMapper.toSubMenuResponseList(subMenus);
    }

    @Operation(
            summary = "Create submenu",
            description = "Creates a new submenu under the selected user's root menu."
    )
    @ApiResponse(responseCode = "201", description = "Submenu created successfully")
    @PostMapping("/api/users/{userId}/submenus")
    @ResponseStatus(HttpStatus.CREATED)
    public void createSubMenu(@PathVariable String userId,
                              @RequestBody CreateSubMenuRequest request) {
        menuManagementService.createSubMenu(userId, request.getName());
    }

    @Operation(
            summary = "Delete submenu",
            description = "Deletes the selected submenu from the user's root menu."
    )
    @ApiResponse(responseCode = "204", description = "Submenu deleted successfully")
    @DeleteMapping("/api/users/{userId}/submenus/{submenuId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubMenu(@PathVariable String userId,
                              @PathVariable String submenuId) {
        menuManagementService.deleteSubMenu(userId, submenuId);
    }

    @Operation(
            summary = "Get submenu details",
            description = "Returns the selected submenu with its applications."
    )
    @ApiResponse(responseCode = "200", description = "Submenu returned successfully")
    @GetMapping("/api/submenus/{submenuId}")
    public RootMenuResponse getSubMenu(@PathVariable String submenuId) {
        Menu submenu = menuManagementService.findSubMenuWithItems(submenuId);

        List<MenuItem> items = submenu.getMenuItems() == null
                ? List.of()
                : submenu.getMenuItems().stream()
                .sorted(Comparator.comparingInt(item ->
                        item.getPositionIndex() == null ? Integer.MAX_VALUE : item.getPositionIndex()))
                .toList();

        return new RootMenuResponse(
                submenu.getName(),
                null,
                null,
                menuMapper.toMenuItemResponseList(items),
                List.of()
        );
    }

    @Operation(
            summary = "Add application to submenu",
            description = "Adds an application to the selected submenu."
    )
    @ApiResponse(responseCode = "201", description = "Application added successfully")
    @PostMapping("/api/submenus/{submenuId}/applications")
    @ResponseStatus(HttpStatus.CREATED)
    public void addApplicationToSubMenu(@PathVariable String submenuId,
                                        @RequestBody AddApplicationRequest request) {
        menuManagementService.addApplicationToSubMenu(submenuId, request.getApplicationId());
    }

    @Operation(
            summary = "Remove application from submenu",
            description = "Removes an application from the selected submenu."
    )
    @ApiResponse(responseCode = "204", description = "Application removed successfully")
    @DeleteMapping("/api/submenus/{submenuId}/applications/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeApplicationFromSubMenu(@PathVariable String submenuId,
                                             @PathVariable String menuItemId) {
        menuManagementService.removeApplicationFromSubMenu(submenuId, menuItemId);
    }

    @Operation(
            summary = "Launch application from submenu",
            description = "Launches the selected application from the submenu."
    )
    @ApiResponse(responseCode = "200", description = "Application launched successfully")
    @PostMapping("/api/submenus/{submenuId}/applications/{menuItemId}/launch")
    public LaunchResponse launchSubMenuApplication(@PathVariable String submenuId,
                                                   @PathVariable String menuItemId) {
        String launchMessage = menuManagementService.launchApplicationFromSubMenu(submenuId, menuItemId);
        return new LaunchResponse(launchMessage);
    }
}