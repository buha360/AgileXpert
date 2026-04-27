package hu.wardanger.devicemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.wardanger.devicemanager.models.request.AddApplicationRequest;
import hu.wardanger.devicemanager.models.request.CreateSubMenuRequest;
import hu.wardanger.devicemanager.models.response.MenuItemResponse;
import hu.wardanger.devicemanager.models.response.SubMenuResponse;
import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.Wallpaper;
import hu.wardanger.devicemanager.mapper.MenuMapper;
import hu.wardanger.devicemanager.service.MenuManagementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MenuController.class)
class MenuControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MenuManagementService menuManagementService;

    @MockitoBean
    private MenuMapper menuMapper;

    @Test
    @DisplayName("GET /api/users/{userId}/menu - visszaadja a root menüt")
    void getRootMenu_shouldReturnRootMenuResponse() throws Exception {
        MenuItem menuItem = new MenuItem();
        menuItem.setId("item-1");
        menuItem.setName("OpenMap");
        menuItem.setPositionIndex(1);

        Menu rootMenu = new Menu();
        rootMenu.setId("menu-1");
        rootMenu.setName("Buha főmenüje");
        rootMenu.setMenuItems(List.of(menuItem));
        rootMenu.setChildMenus(List.of());

        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setId("wallpaper-1");
        wallpaper.setName("Mountain");

        Theme theme = new Theme();
        theme.setId("theme-1");
        theme.setName("Dark");

        UserAccount user = new UserAccount();
        user.setId("user-1");
        user.setRootMenu(rootMenu);
        user.setWallpaper(wallpaper);
        user.setTheme(theme);

        MenuItemResponse menuItemResponse = new MenuItemResponse("item-1", "OpenMap", 1);

        when(menuManagementService.findUserWithRootMenuItems("user-1")).thenReturn(user);
        when(menuMapper.toMenuItemResponseList(List.of(menuItem))).thenReturn(List.of(menuItemResponse));
        when(menuMapper.toSubMenuResponseList(List.of())).thenReturn(List.of());

        mockMvc.perform(get("/api/users/user-1/menu"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuName").value("Buha főmenüje"))
                .andExpect(jsonPath("$.wallpaperName").value("Mountain"))
                .andExpect(jsonPath("$.themeName").value("Dark"))
                .andExpect(jsonPath("$.applications[0].id").value("item-1"))
                .andExpect(jsonPath("$.applications[0].name").value("OpenMap"));
    }

    @Test
    @DisplayName("POST /api/users/{userId}/menu/applications - app hozzáadás a főmenühöz")
    void addApplicationToRootMenu_shouldReturnCreated() throws Exception {
        AddApplicationRequest request = new AddApplicationRequest("app-openmap");

        mockMvc.perform(post("/api/users/user-1/menu/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(menuManagementService).addApplicationToRootMenu("user-1", "app-openmap");
    }

    @Test
    @DisplayName("DELETE /api/users/{userId}/menu/applications/{menuItemId} - app törlés a főmenüből")
    void removeApplicationFromRootMenu_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/user-1/menu/applications/item-1"))
                .andExpect(status().isNoContent());

        verify(menuManagementService).removeApplicationFromRootMenu("user-1", "item-1");
    }

    @Test
    @DisplayName("POST /api/users/{userId}/menu/applications/{menuItemId}/launch - app indítás a főmenüből")
    void launchRootMenuApplication_shouldReturnLaunchMessage() throws Exception {
        when(menuManagementService.launchApplicationFromRootMenu("user-1", "item-1"))
                .thenReturn("OpenMap alkalmazás elindult.");

        mockMvc.perform(post("/api/users/user-1/menu/applications/item-1/launch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OpenMap alkalmazás elindult."));
    }

    @Test
    @DisplayName("GET /api/users/{userId}/submenus - almenük listázása")
    void getSubMenus_shouldReturnSubMenus() throws Exception {
        Menu submenu = new Menu();
        submenu.setId("submenu-1");
        submenu.setName("Játékok");

        SubMenuResponse response = new SubMenuResponse("submenu-1", "Játékok");

        when(menuManagementService.findSubMenus("user-1")).thenReturn(List.of(submenu));
        when(menuMapper.toSubMenuResponseList(List.of(submenu))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/users/user-1/submenus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("submenu-1"))
                .andExpect(jsonPath("$[0].name").value("Játékok"));
    }

    @Test
    @DisplayName("POST /api/users/{userId}/submenus - almenü létrehozása")
    void createSubMenu_shouldReturnCreated() throws Exception {
        CreateSubMenuRequest request = new CreateSubMenuRequest("Játékok");

        mockMvc.perform(post("/api/users/user-1/submenus")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(menuManagementService).createSubMenu("user-1", "Játékok");
    }

    @Test
    @DisplayName("DELETE /api/users/{userId}/submenus/{submenuId} - almenü törlése")
    void deleteSubMenu_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/user-1/submenus/submenu-1"))
                .andExpect(status().isNoContent());

        verify(menuManagementService).deleteSubMenu("user-1", "submenu-1");
    }

    @Test
    @DisplayName("GET /api/submenus/{submenuId} - adott almenü lekérdezése")
    void getSubMenu_shouldReturnSubMenuDetails() throws Exception {
        MenuItem menuItem = new MenuItem();
        menuItem.setId("item-1");
        menuItem.setName("Minesweeper");
        menuItem.setPositionIndex(1);

        Menu submenu = new Menu();
        submenu.setId("submenu-1");
        submenu.setName("Játékok");
        submenu.setMenuItems(List.of(menuItem));

        MenuItemResponse menuItemResponse = new MenuItemResponse("item-1", "Minesweeper", 1);

        when(menuManagementService.findSubMenuWithItems("submenu-1")).thenReturn(submenu);
        when(menuMapper.toMenuItemResponseList(List.of(menuItem))).thenReturn(List.of(menuItemResponse));

        mockMvc.perform(get("/api/submenus/submenu-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.menuName").value("Játékok"))
                .andExpect(jsonPath("$.applications[0].name").value("Minesweeper"));
    }

    @Test
    @DisplayName("POST /api/submenus/{submenuId}/applications - app hozzáadás almenühöz")
    void addApplicationToSubMenu_shouldReturnCreated() throws Exception {
        AddApplicationRequest request = new AddApplicationRequest("app-minesweeper");

        mockMvc.perform(post("/api/submenus/submenu-1/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        verify(menuManagementService).addApplicationToSubMenu("submenu-1", "app-minesweeper");
    }

    @Test
    @DisplayName("DELETE /api/submenus/{submenuId}/applications/{menuItemId} - app törlés almenüből")
    void removeApplicationFromSubMenu_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/submenus/submenu-1/applications/item-1"))
                .andExpect(status().isNoContent());

        verify(menuManagementService).removeApplicationFromSubMenu("submenu-1", "item-1");
    }

    @Test
    @DisplayName("POST /api/submenus/{submenuId}/applications/{menuItemId}/launch - app indítás almenüből")
    void launchSubMenuApplication_shouldReturnLaunchMessage() throws Exception {
        when(menuManagementService.launchApplicationFromSubMenu("submenu-1", "item-1"))
                .thenReturn("Aknakereső alkalmazás elindult.");

        mockMvc.perform(post("/api/submenus/submenu-1/applications/item-1/launch"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Aknakereső alkalmazás elindult."));
    }
}