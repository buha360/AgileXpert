package hu.wardanger.devicemanager.service;

import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.Wallpaper;
import hu.wardanger.devicemanager.repository.ThemeRepository;
import hu.wardanger.devicemanager.repository.UserAccountRepository;
import hu.wardanger.devicemanager.repository.WallpaperRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class CustomizationService {

    private final UserAccountRepository userAccountRepository;
    private final WallpaperRepository wallpaperRepository;
    private final ThemeRepository themeRepository;

    public CustomizationService(UserAccountRepository userAccountRepository,
                                WallpaperRepository wallpaperRepository,
                                ThemeRepository themeRepository) {
        this.userAccountRepository = userAccountRepository;
        this.wallpaperRepository = wallpaperRepository;
        this.themeRepository = themeRepository;
    }

    @Transactional(readOnly = true)
    public List<Wallpaper> findAllWallpapers() {
        return wallpaperRepository.findAll().stream()
                .sorted(Comparator.comparing(Wallpaper::getName))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Theme> findAllThemes() {
        return themeRepository.findAll().stream()
                .sorted(Comparator.comparing(Theme::getName))
                .toList();
    }

    @Transactional
    public void setWallpaperForUser(String userId, String wallpaperId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        Wallpaper wallpaper = wallpaperRepository.findById(wallpaperId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen háttérkép."));

        user.setWallpaper(wallpaper);
        userAccountRepository.save(user);
    }

    @Transactional
    public void setThemeForUser(String userId, String themeId) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen arculat."));

        user.setTheme(theme);
        userAccountRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserAccount findUserWithCustomization(String userId) {
        return userAccountRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));
    }

    @Transactional(readOnly = true)
    public Wallpaper findDefaultWallpaper() {
        return findAllWallpapers().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nincs seedelt háttérkép."));
    }

    @Transactional(readOnly = true)
    public Theme findDefaultTheme() {
        return findAllThemes().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Nincs seedelt arculat."));
    }
}