package hu.wardanger.devicemanager.cli;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.service.MenuManagementService;
import org.springframework.stereotype.Component;

@Component
public class UserSessionCli {

    private final MenuManagementService menuManagementService;

    public UserSessionCli(MenuManagementService menuManagementService) {
        this.menuManagementService = menuManagementService;
    }

    public void openUserSession(java.util.Scanner scanner, UserAccount user) {
        boolean running = true;

        while (running) {
            printUserMenu(user);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> showRootMenu(user);
                case "2" -> System.out.println("Almenük listázása még nincs implementálva.");
                case "3" -> System.out.println("Almenü létrehozása még nincs implementálva.");
                case "4" -> System.out.println("Almenü törlése még nincs implementálva.");
                case "5" -> addApplicationToRootMenu(scanner, user);
                case "6" -> System.out.println("Alkalmazás törlése még nincs implementálva.");
                case "7" -> System.out.println("Alkalmazás indítása még nincs implementálva.");
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
        System.out.println("1. Fömenü megtekintése");
        System.out.println("2. Almenük listázása");
        System.out.println("3. Almenü létrehozása");
        System.out.println("4. Almenü törlése");
        System.out.println("5. Közvetlen alkalmazás hozzáadása");
        System.out.println("6. Közvetlen alkalmazás törlése");
        System.out.println("7. Alkalmazás indítása");
        System.out.println("0. Vissza");
        System.out.print("Választás: ");
    }

    private void showRootMenu(UserAccount user) {
        UserAccount loadedUser = menuManagementService.findUserWithRootMenuItems(user.getId());
        Menu rootMenu = loadedUser.getRootMenu();

        if (rootMenu == null) {
            System.out.println("A felhasználóhoz még nincs fömenü rendelve.");
            return;
        }

        System.out.println("Fömenü neve: " + rootMenu.getName());

        if (rootMenu.getMenuItems() == null || rootMenu.getMenuItems().isEmpty()) {
            System.out.println("Közvetlen alkalmazások: nincsenek");
        } else {
            System.out.println("Közvetlen alkalmazások:");
            for (int i = 0; i < rootMenu.getMenuItems().size(); i++) {
                System.out.println((i + 1) + ". " + rootMenu.getMenuItems().get(i).getName());
            }
        }

        if (rootMenu.getChildMenus() == null || rootMenu.getChildMenus().isEmpty()) {
            System.out.println("Almenük: nincsenek");
        } else {
            System.out.println("Almenük:");
            for (int i = 0; i < rootMenu.getChildMenus().size(); i++) {
                System.out.println((i + 1) + ". " + rootMenu.getChildMenus().get(i).getName());
            }
        }
    }

    private void addApplicationToRootMenu(java.util.Scanner scanner, UserAccount user) {
        var applications = menuManagementService.findAllApplications();

        if (applications.isEmpty()) {
            System.out.println("Nincs elérhető alkalmazás.");
            return;
        }

        System.out.println("Elérhető alkalmazások:");
        for (int i = 0; i < applications.size(); i++) {
            System.out.println((i + 1) + ". " + applications.get(i).getName());
        }

        System.out.print("Add meg a hozzáadandó alkalmazás sorszámát: ");
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > applications.size()) {
                System.out.println("Érvénytelen sorszám.");
                return;
            }

            var selectedApplication = applications.get(selectedIndex - 1);
            menuManagementService.addApplicationToRootMenu(user.getId(), selectedApplication.getId());

            System.out.println("Alkalmazás hozzáadva a fömenühöz: " + selectedApplication.getName());
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}