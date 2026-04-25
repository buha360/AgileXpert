package hu.wardanger.devicemanager.cli;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.entity.SmartApplication;
import hu.wardanger.devicemanager.service.MenuManagementService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class SubMenuSessionCli {

    private final MenuManagementService menuManagementService;

    public SubMenuSessionCli(MenuManagementService menuManagementService) {
        this.menuManagementService = menuManagementService;
    }

    public void openSubMenuSession(Scanner scanner, Menu submenu) {
        boolean running = true;

        while (running) {
            printSubMenu(submenu);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listApplications(submenu);
                case "2" -> addApplication(scanner, submenu);
                case "3" -> removeApplication(scanner, submenu);
                case "4" -> launchApplication(scanner, submenu);
                case "0" -> {
                    running = false;
                    System.out.println("Visszatérés a felhasználói menübe...");
                }
                default -> System.out.println("Érvénytelen választás.");
            }

            System.out.println();
        }
    }

    private void printSubMenu(Menu submenu) {
        System.out.println("=== " + submenu.getName().toUpperCase() + " ALMENÜ ===");
        System.out.println("1. Alkalmazások listázása");
        System.out.println("2. Alkalmazás hozzáadása");
        System.out.println("3. Alkalmazás törlése");
        System.out.println("4. Alkalmazás indítása");
        System.out.println("0. Vissza");
        System.out.print("Választás: ");
    }

    private void listApplications(Menu submenu) {
        List<MenuItem> items = getSubMenuItems(submenu);

        if (items.isEmpty()) {
            System.out.println("Nincs még alkalmazás az almenüben.");
            return;
        }

        printMenuItems(items);
    }

    private void addApplication(Scanner scanner, Menu submenu) {
        List<SmartApplication> applications = menuManagementService.findAllApplications();

        if (applications.isEmpty()) {
            System.out.println("Nincs elérhető alkalmazás.");
            return;
        }

        printApplications(applications);

        SmartApplication selectedApplication = selectApplicationByIndex(
                scanner,
                applications
        );

        if (selectedApplication == null) {
            return;
        }

        try {
            menuManagementService.addApplicationToSubMenu(submenu.getId(), selectedApplication.getId());
            System.out.println("Alkalmazás hozzáadva az almenühöz: " + selectedApplication.getName());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void removeApplication(Scanner scanner, Menu submenu) {
        List<MenuItem> items = getSubMenuItems(submenu);

        if (items.isEmpty()) {
            System.out.println("Nincs törölhető alkalmazás az almenüben.");
            return;
        }

        printMenuItems(items);

        MenuItem selectedItem = selectMenuItemByIndex(
                scanner,
                items,
                "Add meg a törlendő alkalmazás sorszámát: "
        );

        if (selectedItem == null) {
            return;
        }

        try {
            menuManagementService.removeApplicationFromSubMenu(submenu.getId(), selectedItem.getId());
            System.out.println("Alkalmazás törölve az almenüből: " + selectedItem.getName());
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void launchApplication(Scanner scanner, Menu submenu) {
        List<MenuItem> items = getSubMenuItems(submenu);

        if (items.isEmpty()) {
            System.out.println("Nincs indítható alkalmazás az almenüben.");
            return;
        }

        printMenuItems(items);

        MenuItem selectedItem = selectMenuItemByIndex(
                scanner,
                items,
                "Add meg az indítandó alkalmazás sorszámát: "
        );

        if (selectedItem == null) {
            return;
        }

        try {
            String launchMessage = menuManagementService.launchApplicationFromSubMenu(submenu.getId(), selectedItem.getId());
            System.out.println(launchMessage);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private List<MenuItem> getSubMenuItems(Menu submenu) {
        Menu loadedSubmenu = menuManagementService.findSubMenuWithItems(submenu.getId());
        return loadedSubmenu.getMenuItems() == null ? List.of() : loadedSubmenu.getMenuItems();
    }

    private void printMenuItems(List<MenuItem> items) {
        System.out.println("Alkalmazások:");
        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + items.get(i).getName());
        }
    }

    private void printApplications(List<SmartApplication> applications) {
        System.out.println("Elérhető alkalmazások:");
        for (int i = 0; i < applications.size(); i++) {
            System.out.println((i + 1) + ". " + applications.get(i).getName());
        }
    }

    private MenuItem selectMenuItemByIndex(Scanner scanner, List<MenuItem> items, String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > items.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return items.get(selectedIndex - 1);
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
}