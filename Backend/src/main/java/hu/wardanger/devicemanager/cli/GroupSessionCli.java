package hu.wardanger.devicemanager.cli;

import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.entity.UserRole;
import hu.wardanger.devicemanager.service.UserAccountService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class GroupSessionCli {

    private final UserAccountService userAccountService;
    private final UserSessionCli userSessionCli;

    public GroupSessionCli(UserAccountService userAccountService,
                           UserSessionCli userSessionCli) {
        this.userAccountService = userAccountService;
        this.userSessionCli = userSessionCli;
    }

    public void openGroupSession(Scanner scanner, UserGroup group) {
        boolean running = true;

        while (running) {
            printGroupMenu(group);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listUsers(group);
                case "2" -> createUser(scanner, group);
                case "3" -> deleteUser(scanner, group);
                case "4" -> enterAsUser(scanner, group);
                case "0" -> {
                    running = false;
                    System.out.println("Kilépés a groupból...");
                }
                default -> System.out.println("Érvénytelen választás.");
            }

            System.out.println();
        }
    }

    private void printGroupMenu(UserGroup group) {
        System.out.println("=== " + group.getName().toUpperCase() + " GROUP MENÜ ===");
        System.out.println("1. Felhasználók listázása");
        System.out.println("2. Felhasználó létrehozása");
        System.out.println("3. Felhasználó törlése");
        System.out.println("4. Belépés felhasználóként");
        System.out.println("0. Vissza");
        System.out.print("Választás: ");
    }

    private void listUsers(UserGroup group) {
        List<UserAccount> users = userAccountService.findUsersByGroup(group.getId());

        if (users.isEmpty()) {
            System.out.println("Nincs még felhasználó a groupban.");
            return;
        }

        System.out.println("Felhasználók:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " [" + users.get(i).getRole() + "]");
        }
    }

    private void createUser(Scanner scanner, UserGroup group) {
        List<UserAccount> users = userAccountService.findUsersByGroup(group.getId());
        UserAccount admin = users.stream()
                .filter(u -> u.getRole() == UserRole.ADMIN)
                .findFirst()
                .orElse(null);

        if (admin == null) {
            System.out.println("Nincs admin user a groupban.");
            return;
        }

        System.out.print("Add meg az új felhasználó nevét: ");
        String userName = scanner.nextLine();

        UserAccount created = userAccountService.createUser(group.getId(), userName);
        System.out.println("Felhasználó létrehozva: " + created.getName());
    }

    private void deleteUser(Scanner scanner, UserGroup group) {
        List<UserAccount> users = userAccountService.findUsersByGroup(group.getId());

        if (users.isEmpty()) {
            System.out.println("Nincs törölhető felhasználó.");
            return;
        }

        System.out.println("Felhasználók:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " [" + users.get(i).getRole() + "]");
        }

        System.out.print("Add meg a törlendő felhasználó sorszámát: ");
        String inputIndex = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(inputIndex);

            if (selectedIndex < 1 || selectedIndex > users.size()) {
                System.out.println("Érvénytelen sorszám.");
                return;
            }

            UserAccount selectedUser = users.get(selectedIndex - 1);

            if (selectedUser.getRole() == UserRole.ADMIN) {
                System.out.println("Az admin felhasználó nem törölhető.");
                return;
            }

            userAccountService.deleteUserById(selectedUser.getId());
            System.out.println("Felhasználó törölve: " + selectedUser.getName());
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
        }
    }

    private void enterAsUser(Scanner scanner, UserGroup group) {
        List<UserAccount> users = userAccountService.findUsersByGroup(group.getId());

        if (users.isEmpty()) {
            System.out.println("Nincs még felhasználó a groupban.");
            return;
        }

        System.out.println("Felhasználók:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " [" + users.get(i).getRole() + "]");
        }

        System.out.print("Add meg a kiválasztott felhasználó sorszámát: ");
        String inputIndex = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(inputIndex);

            if (selectedIndex < 1 || selectedIndex > users.size()) {
                System.out.println("Érvénytelen sorszám.");
                return;
            }

            UserAccount selectedUser = users.get(selectedIndex - 1);
            UserAccount loadedUser = userAccountService.findDetailedUserById(selectedUser.getId());
            userSessionCli.openUserSession(scanner, loadedUser);

        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
        }
    }
}