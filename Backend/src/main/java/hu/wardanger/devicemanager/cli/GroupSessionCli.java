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

    public void openGroupSession(Scanner scanner, UserGroup group, UserAccount currentUser) {
        if (currentUser.getRole() == UserRole.MEMBER) {
            System.out.println("Belépés felhasználóként: " + currentUser.getName());
            userSessionCli.openUserSession(scanner, currentUser);
            return;
        }

        boolean running = true;

        while (running) {
            printGroupMenu(group, currentUser);
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listUsers(group);
                case "2" -> createUser(scanner, group, currentUser);
                case "3" -> deleteUser(scanner, group, currentUser);
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

    private void printGroupMenu(UserGroup group, UserAccount currentUser) {
        System.out.println("=== " + group.getName().toUpperCase() + " GROUP MENÜ ===");
        System.out.println("Aktuális felhasználó: " + currentUser.getName() + " [" + currentUser.getRole() + "]");
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

        printUsers(users);
    }

    private void createUser(Scanner scanner, UserGroup group, UserAccount currentUser) {
        if (requireAdmin(currentUser)) {
            return;
        }

        System.out.print("Add meg az új felhasználó nevét: ");
        String userName = scanner.nextLine();

        System.out.print("Add meg az új felhasználó jelszavát: ");
        String password = scanner.nextLine();

        UserAccount created = userAccountService.createUser(group.getId(), userName, password);
        System.out.println("Felhasználó létrehozva: " + created.getName());
    }

    private void deleteUser(Scanner scanner, UserGroup group, UserAccount currentUser) {
        if (requireAdmin(currentUser)) {
            return;
        }

        List<UserAccount> users = userAccountService.findUsersByGroup(group.getId());

        if (users.isEmpty()) {
            System.out.println("Nincs törölhető felhasználó.");
            return;
        }

        printUsers(users);

        UserAccount selectedUser = selectUserByIndex(scanner, users, "Add meg a törlendő felhasználó sorszámát: ");
        if (selectedUser == null) {
            return;
        }

        if (selectedUser.getRole() == UserRole.ADMIN) {
            System.out.println("Az admin felhasználó nem törölhető.");
            return;
        }

        userAccountService.deleteUserById(selectedUser.getId());
        System.out.println("Felhasználó törölve: " + selectedUser.getName());
    }

    private void enterAsUser(Scanner scanner, UserGroup group) {
        List<UserAccount> users = userAccountService.findUsersByGroup(group.getId());

        if (users.isEmpty()) {
            System.out.println("Nincs még felhasználó a groupban.");
            return;
        }

        printUsers(users);

        UserAccount selectedUser = selectUserByIndex(scanner, users, "Add meg a kiválasztott felhasználó sorszámát: ");
        if (selectedUser == null) {
            return;
        }

        System.out.print("Add meg a jelszót: ");
        String password = scanner.nextLine();

        try {
            UserAccount authenticatedUser = userAccountService.authenticateUser(selectedUser.getId(), password);
            userSessionCli.openUserSession(scanner, authenticatedUser);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private boolean requireAdmin(UserAccount currentUser) {
        if (currentUser.getRole() != UserRole.ADMIN) {
            System.out.println("Ehhez admin jogosultság szükséges.");
            return true;
        }
        return false;
    }

    private void printUsers(List<UserAccount> users) {
        System.out.println("Felhasználók:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " [" + users.get(i).getRole() + "]");
        }
    }

    private UserAccount selectUserByIndex(Scanner scanner, List<UserAccount> users, String prompt) {
        System.out.print(prompt);
        String inputIndex = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(inputIndex);

            if (selectedIndex < 1 || selectedIndex > users.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return users.get(selectedIndex - 1);
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
            return null;
        }
    }
}