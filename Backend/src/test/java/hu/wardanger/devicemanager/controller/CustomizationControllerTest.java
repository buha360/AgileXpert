package hu.wardanger.devicemanager.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hu.wardanger.devicemanager.dto.request.SelectThemeRequest;
import hu.wardanger.devicemanager.dto.request.SelectWallpaperRequest;
import hu.wardanger.devicemanager.dto.response.ThemeResponse;
import hu.wardanger.devicemanager.dto.response.WallpaperResponse;
import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.Wallpaper;
import hu.wardanger.devicemanager.mapper.CustomizationMapper;
import hu.wardanger.devicemanager.service.CustomizationService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomizationController.class)
class CustomizationControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomizationService customizationService;

    @MockitoBean
    private CustomizationMapper customizationMapper;

    @Test
    @DisplayName("GET /api/customization/wallpapers - háttérképek listázása")
    void getAllWallpapers_shouldReturnWallpapers() throws Exception {
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setId("wallpaper-1");
        wallpaper.setName("Mountain");

        WallpaperResponse response = new WallpaperResponse("wallpaper-1", "Mountain");

        when(customizationService.findAllWallpapers()).thenReturn(List.of(wallpaper));
        when(customizationMapper.toWallpaperResponseList(List.of(wallpaper))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/customization/wallpapers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("wallpaper-1"))
                .andExpect(jsonPath("$[0].name").value("Mountain"));
    }

    @Test
    @DisplayName("GET /api/customization/themes - arculatok listázása")
    void getAllThemes_shouldReturnThemes() throws Exception {
        Theme theme = new Theme();
        theme.setId("theme-1");
        theme.setName("Dark");

        ThemeResponse response = new ThemeResponse("theme-1", "Dark");

        when(customizationService.findAllThemes()).thenReturn(List.of(theme));
        when(customizationMapper.toThemeResponseList(List.of(theme))).thenReturn(List.of(response));

        mockMvc.perform(get("/api/customization/themes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("theme-1"))
                .andExpect(jsonPath("$[0].name").value("Dark"));
    }

    @Test
    @DisplayName("PUT /api/users/{userId}/wallpaper - háttérkép kiválasztása")
    void selectWallpaper_shouldReturnNoContent() throws Exception {
        SelectWallpaperRequest request = new SelectWallpaperRequest("wallpaper-1");

        mockMvc.perform(put("/api/users/user-1/wallpaper")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(customizationService).setWallpaperForUser("user-1", "wallpaper-1");
    }

    @Test
    @DisplayName("PUT /api/users/{userId}/theme - arculat kiválasztása")
    void selectTheme_shouldReturnNoContent() throws Exception {
        SelectThemeRequest request = new SelectThemeRequest("theme-1");

        mockMvc.perform(put("/api/users/user-1/theme")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNoContent());

        verify(customizationService).setThemeForUser("user-1", "theme-1");
    }
}