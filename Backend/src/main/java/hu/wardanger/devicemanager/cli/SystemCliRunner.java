package hu.wardanger.devicemanager.cli;

import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.service.GroupService;
import hu.wardanger.devicemanager.service.UserAccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class SystemCliRunner implements CommandLineRunner {

    private final GroupService groupService;
    private final GroupSessionCli groupSessionCli;
    private final UserAccountService userAccountService;

    public SystemCliRunner(GroupService groupService,
                           GroupSessionCli groupSessionCli,
                           UserAccountService userAccountService) {
        this.groupService = groupService;
        this.groupSessionCli = groupSessionCli;
        this.userAccountService = userAccountService;
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("A szolgáltatás elindult!");
        System.out.println("Adatbázis kapcsolat és Liquibase rendben.");
        System.out.println();

        while (running) {
            printSystemMenu();
            String input = scanner.nextLine();

            switch (input) {
                case "1" -> listGroups();
                case "2" -> registerGroup(scanner);
                case "3" -> enterGroup(scanner);
                case "0" -> {
                    running = false;
                    System.out.println("Kilépés...");
                }
                default -> System.out.println("Érvénytelen választás.");
            }

            System.out.println();
        }
    }

    private void printSystemMenu() {
        System.out.println("=== RENDSZER MENÜ ===");
        System.out.println("1. Groupok listázása");
        System.out.println("2. Group regisztrálása");
        System.out.println("3. Belépés groupba");
        System.out.println("0. Kilépés");
        System.out.print("Választás: ");
    }

    private void listGroups() {
        List<UserGroup> groups = groupService.findAllGroups();

        if (groups.isEmpty()) {
            System.out.println("Nincs még regisztrált group.");
            return;
        }

        printGroups(groups);
    }

    private void registerGroup(Scanner scanner) {
        System.out.print("Add meg a group nevét: ");
        String groupName = scanner.nextLine();

        System.out.print("Add meg az access code-ot: ");
        String accessCode = scanner.nextLine();

        System.out.print("Add meg az admin felhasználó nevét: ");
        String adminUserName = scanner.nextLine();

        System.out.print("Add meg az admin jelszavát: ");
        String adminPassword = scanner.nextLine();

        UserGroup createdGroup = groupService.registerGroup(groupName, accessCode, adminUserName, adminPassword);
        System.out.println("Group létrehozva: " + createdGroup.getName());
        System.out.println("Az admin felhasználó automatikusan létrejött: " + adminUserName);
    }

    private void enterGroup(Scanner scanner) {
        List<UserGroup> groups = groupService.findAllGroups();

        if (groups.isEmpty()) {
            System.out.println("Nincs még regisztrált group.");
            return;
        }

        printGroups(groups);

        UserGroup selectedGroup = selectGroupByIndex(scanner, groups);
        if (selectedGroup == null) {
            return;
        }

        System.out.print("Add meg az access code-ot: ");
        String accessCode = scanner.nextLine();

        boolean valid = groupService.validateAccessCode(selectedGroup.getId(), accessCode);
        if (!valid) {
            System.out.println("Hibás access code.");
            return;
        }

        List<UserAccount> users = userAccountService.findUsersByGroup(selectedGroup.getId());

        if (users.isEmpty()) {
            System.out.println("Nincs még felhasználó a groupban.");
            return;
        }

        printUsers(users);

        UserAccount selectedUser = selectUserByIndex(scanner, users);
        if (selectedUser == null) {
            return;
        }

        System.out.print("Add meg a jelszót: ");
        String password = scanner.nextLine();

        try {
            UserAccount authenticatedUser = userAccountService.authenticateUser(selectedUser.getId(), password);
            groupSessionCli.openGroupSession(scanner, selectedGroup, authenticatedUser);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    private void printGroups(List<UserGroup> groups) {
        System.out.println("Groupok:");
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).getName());
        }
    }

    private void printUsers(List<UserAccount> users) {
        System.out.println("Felhasználók:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + " [" + users.get(i).getRole() + "]");
        }
    }

    private UserGroup selectGroupByIndex(Scanner scanner, List<UserGroup> groups) {
        System.out.print("Add meg a kiválasztott group sorszámát: ");
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

            if (selectedIndex < 1 || selectedIndex > groups.size()) {
                System.out.println("Érvénytelen sorszám.");
                return null;
            }

            return groups.get(selectedIndex - 1);
        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
            return null;
        }
    }

    private UserAccount selectUserByIndex(Scanner scanner, List<UserAccount> users) {
        System.out.print("Add meg a kiválasztott felhasználó sorszámát: ");
        String input = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(input);

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