package hu.wardanger.devicemanager.cli;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.entity.SmartApplication;
import hu.wardanger.devicemanager.entity.Theme;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.Wallpaper;
import hu.wardanger.devicemanager.service.CustomizationService;
import hu.wardanger.devicemanager.service.MenuManagementService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class UserSessionCli {

    private final MenuManagementService menuManagementService;
    private final SubMenuSessionCli subMenuSessionCli;
    private final CustomizationService customizationService;
    private final TransactionTemplate transactionTemplate;

    public UserSessionCli(MenuManagementService menuManagementService,
                          SubMenuSessionCli subMenuSessionCli,
                          CustomizationService customizationService,
                          TransactionTemplate transactionTemplate) {
        this.menuManagementService = menuManagementService;
        this.subMenuSessionCli = subMenuSessionCli;
        this.customizationService = customizationService;
        this.transactionTemplate = transactionTemplate;
    }

    public void openUserSession(Scanner scanner, UserAccount user) {
        boolean running = true;

        while (running) {
            printUserMenu(user);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showRootMenu(user);
                case "2" -> listSubMenus(user);
                case "3" -> createSubMenu(scanner, user);
                case "4" -> deleteSubMenu(scanner, user);
                case "5" -> openSubMenu(scanner, user);
                case "6" -> addApplicationToRootMenu(scanner, user);
                case "7" -> removeApplicationFromRootMenu(scanner, user);
                case "8" -> launchApplication(scanner, user);
                case "9" -> selectWallpaper(scanner, user);
                case "10" -> selectTheme(scanner, user);
                case "0" -> {
                    running = false;
                    System.out.println("Visszatérés a főmenübe...");
                }
                default -> System.out.println("Érvénytelen választás.");
            }

            System.out.println();
        }
    }

    private void printUserMenu(UserAccount user) {
        System.out.println("Üdvözöljük, " + user.getName() + "!");
        System.out.println("=== " + user.getName().toUpperCase() + " MENÜJE ===");
        System.out.println("1. Főmenü megtekintése");
        System.out.println("2. Almenük listázása");
        System.out.println("3. Almenü létrehozása");
        System.out.println("4. Almenü törlése");
        System.out.println("5. Almenü megnyitása");
        System.out.println("6. Közvetlen alkalmazás hozzáadása");
        System.out.println("7. Közvetlen alkalmazás törlése");
        System.out.println("8. Alkalmazás indítása");
        System.out.println("9. Háttérkép kiválasztása");
        System.out.println("10. Arculat kiválasztása");
        System.out.println("0. Vissza");
        System.out.print("Választás: ");
    }

    private void showRootMenu(UserAccount user) {
        inTransaction(() -> {
            UserAccount loadedUser = menuManagementService.findUserWithRootMenuItems(user.getId());
            Menu rootMenu = loadedUser.getRootMenu();

            if (rootMenu == null) {
                System.out.println("A felhasználóhoz még nincs főmenü rendelve.");
                return;
            }

            System.out.println("Főmenü neve: " + rootMenu.getName());
            System.out.println("Aktuális háttérkép: " + loadedUser.getWallpaper().getName());
            System.out.println("Aktuális arculat: " + loadedUser.getTheme().getName());

            List<MenuItem> rootItems = getSortedRootMenuItems(loadedUser);
            List<Menu> subMenus = getSubMenus(loadedUser);

            if (rootItems.isEmpty()) {
                System.out.println("Közvetlen alkalmazások: nincsenek");
            } else {
                printMenuItems("Közvetlen alkalmazások:", rootItems);
            }

            if (subMenus.isEmpty()) {
                System.out.println("Almenük: nincsenek");
            } else {
                printMenus(subMenus);
            }
        });
    }

    private void listSubMenus(UserAccount user) {
        inTransaction(() -> {
            List<Menu> subMenus = getSubMenus(user);

            if (subMenus.isEmpty()) {
                System.out.println("Nincs még almenü.");
                return;
            }

            printMenus(subMenus);
        });
    }

    private void createSubMenu(Scanner scanner, UserAccount user) {
        System.out.print("Add meg az új almenü nevét: ");
        String submenuName = scanner.nextLine();

        try {
            menuManagementService.createSubMenu(user.getId(), submenuName);
            System.out.println("Almenü létrehozva: " + submenuName);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void deleteSubMenu(Scanner scanner, UserAccount user) {
        AtomicReference<List<Menu>> subMenusRef = new AtomicReference<>();

        inTransaction(() -> subMenusRef.set(getSubMenus(user)));
        List<Menu> subMenus = subMenusRef.get();

        if (subMenus == null || subMenus.isEmpty()) {
            System.out.println("Nincs törölhető almenü.");
            return;
        }

        printMenus(subMenus);

        Menu selectedSubMenu = selectMenuByIndex(scanner, subMenus, "Add meg a törlendő almenü sorszámát: ");
        if (selectedSubMenu == null) {
            return;
        }

        try {
            AtomicReference<Integer> itemCountRef = new AtomicReference<>(0);

            inTransaction(() -> {
                Menu loadedSubMenu = menuManagementService.findSubMenuWithItems(selectedSubMenu.getId());
                int itemCount = loadedSubMenu.getMenuItems() == null ? 0 : loadedSubMenu.getMenuItems().size();
                itemCountRef.set(itemCount);
            });

            int itemCount = itemCountRef.get();

            if (itemCount > 0) {
                System.out.println("Az almenü " + itemCount + " alkalmazást tartalmaz.");
                System.out.print("Biztosan törölni szeretnéd? (i/n): ");
                String confirm = scanner.nextLine();

                if (!confirm.equalsIgnoreCase("i")) {
                    System.out.println("Törlés megszakítva.");
                    return;
                }
            }

            menuManagementService.deleteSubMenu(user.getId(), selectedSubMenu.getId());
            System.out.println("Almenü törölve: " + selectedSubMenu.getName());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void openSubMenu(Scanner scanner, UserAccount user) {
        AtomicReference<List<Menu>> subMenusRef = new AtomicReference<>();

        inTransaction(() -> subMenusRef.set(getSubMenus(user)));
        List<Menu> subMenus = subMenusRef.get();

        if (subMenus == null || subMenus.isEmpty()) {
            System.out.println("Nincs megnyitható almenü.");
            return;
        }

        printMenus(subMenus);

        Menu selectedSubMenu = selectMenuByIndex(scanner, subMenus, "Add meg a megnyitandó almenü sorszámát: ");
        if (selectedSubMenu == null) {
            return;
        }

        subMenuSessionCli.openSubMenuSession(scanner, selectedSubMenu);
    }

    private void addApplicationToRootMenu(Scanner scanner, UserAccount user) {
        List<SmartApplication> applications = menuManagementService.findAllApplications();

        if (applications.isEmpty()) {
            System.out.println("Nincs elérhető alkalmazás.");
            return;
        }

        printApplications(applications);

        SmartApplication selectedApplication = selectApplicationByIndex(scanner, applications);

        if (selectedApplication == null) {
            return;
        }

        try {
            menuManagementService.addApplicationToRootMenu(user.getId(), selectedApplication.getId());
            System.out.println("Alkalmazás hozzáadva a főmenühöz: " + selectedApplication.getName());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removeApplicationFromRootMenu(Scanner scanner, UserAccount user) {
        List<MenuItem> menuItems = menuManagementService.findRootMenuItems(user.getId());

        if (menuItems.isEmpty()) {
            System.out.println("Nincs törölhető alkalmazás a főmenüben.");
            return;
        }

        printMenuItems("Főmenü alkalmazások:", menuItems);

        MenuItem selectedMenuItem = selectMenuItemByIndex(
                scanner,
                menuItems,
                "Add meg a törlendő alkalmazás sorszámát: "
        );

        if (selectedMenuItem == null) {
            return;
        }

        try {
            menuManagementService.removeApplicationFromRootMenu(user.getId(), selectedMenuItem.getId());
            System.out.println("Alkalmazás törölve a főmenüből: " + selectedMenuItem.getName());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void launchApplication(Scanner scanner, UserAccount user) {
        List<MenuItem> menuItems = menuManagementService.findRootMenuItems(user.getId());

        if (menuItems.isEmpty()) {
            System.out.println("Nincs indítható alkalmazás a főmenüben.");
            return;
        }

        printMenuItems("Főmenü alkalmazások:", menuItems);

        MenuItem selectedMenuItem = selectMenuItemByIndex(
                scanner,
                menuItems,
                "Add meg az indítandó alkalmazás sorszámát: "
        );

        if (selectedMenuItem == null) {
            return;
        }

        try {
            String launchMessage = menuManagementService.launchApplicationFromRootMenu(user.getId(), selectedMenuItem.getId());
            System.out.println(launchMessage);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void selectWallpaper(Scanner scanner, UserAccount user) {
        List<Wallpaper> wallpapers = customizationService.findAllWallpapers();

        if (wallpapers.isEmpty()) {
            System.out.println("Nincs elérhető háttérkép.");
            return;
        }

        printWallpapers(wallpapers);

        Wallpaper selectedWallpaper = selectWallpaperByIndex(scanner, wallpapers);

        if (selectedWallpaper == null) {
            return;
        }

        try {
            customizationService.setWallpaperForUser(user.getId(), selectedWallpaper.getId());
            System.out.println("Háttérkép beállítva: " + selectedWallpaper.getName());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void selectTheme(Scanner scanner, UserAccount user) {
        List<Theme> themes = customizationService.findAllThemes();

        if (themes.isEmpty()) {
            System.out.println("Nincs elérhető arculat.");
            return;
        }

        printThemes(themes);

        Theme selectedTheme = selectThemeByIndex(scanner, themes);

        if (selectedTheme == null) {
            return;
        }

        try {
            customizationService.setThemeForUser(user.getId(), selectedTheme.getId());
            System.out.println("Arculat beállítva: " + selectedTheme.getName());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<Menu> getSubMenus(UserAccount user) {
        return menuManagementService.findSubMenus(user.getId());
    }

    private List<MenuItem> getSortedRootMenuItems(UserAccount user) {
        Menu rootMenu = user.getRootMenu();

        if (rootMenu == null || rootMenu.getMenuItems() == null) {
            return List.of();
        }

        return rootMenu.getMenuItems().stream()
                .sorted(Comparator.comparingInt(item ->
                        item.getPositionIndex() == null ? Integer.MAX_VALUE : item.getPositionIndex()))
                .toList();
    }

    private void printMenus(List<Menu> menus) {
        System.out.println("Almenük:");
        for (int i = 0; i < menus.size(); i++) {
            System.out.println((i + 1) + ". " + menus.get(i).getName());
        }
    }

    private void printMenuItems(String title, List<MenuItem> menuItems) {
        System.out.println(title);
        for (int i = 0; i < menuItems.size(); i++) {
            System.out.println((i + 1) + ". " + menuItems.get(i).getName());
        }
    }

    private void printApplications(List<SmartApplication> applications) {
        System.out.println("Elérhető alkalmazások:");
        for (int i = 0; i < applications.size(); i++) {
            System.out.println((i + 1) + ". " + applications.get(i).getName());
        }
    }

    private void printWallpapers(List<Wallpaper> wallpapers) {
        System.out.println("Elérhető háttérképek:");
        for (int i = 0; i < wallpapers.size(); i++) {
            System.out.println((i + 1) + ". " + wallpapers.get(i).getName());
        }
    }

    private void printThemes(List<Theme> themes) {
        System.out.println("Elérhető arculatok:");
        for (int i = 0; i < themes.size(); i++) {
            System.out.println((i + 1) + ". " + themes.get(i).getName());
        }
    }

    private Menu selectMenuByIndex(Scanner scanner, List<Menu> menus, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > menus.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return menus.get(selectedIndex - 1);
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
            return null;
        }
    }

    private MenuItem selectMenuItemByIndex(Scanner scanner, List<MenuItem> menuItems, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > menuItems.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return menuItems.get(selectedIndex - 1);
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
            return null;
        }
    }

    private SmartApplication selectApplicationByIndex(Scanner scanner, List<SmartApplication> applications) {
        System.out.print("Add meg a hozzáadandó alkalmazás sorszámát: ");
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > applications.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return applications.get(selectedIndex - 1);
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
            return null;
        }
    }

    private Wallpaper selectWallpaperByIndex(Scanner scanner, List<Wallpaper> wallpapers) {
        System.out.print("Add meg a kiválasztott háttérkép sorszámát: ");
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > wallpapers.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return wallpapers.get(selectedIndex - 1);
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
            return null;
        }
    }

    private Theme selectThemeByIndex(Scanner scanner, List<Theme> themes) {
        System.out.print("Add meg a kiválasztott arculat sorszámát: ");
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > themes.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return themes.get(selectedIndex - 1);
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
            return null;
        }
    }

    private void inTransaction(Runnable action) {
        transactionTemplate.executeWithoutResult(status -> action.run());
    }
}