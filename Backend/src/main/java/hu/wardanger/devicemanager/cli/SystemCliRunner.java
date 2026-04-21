package hu.wardanger.devicemanager.cli;

import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.service.GroupService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class SystemCliRunner implements CommandLineRunner {

    private final GroupService groupService;
    private final GroupSessionCli groupSessionCli;

    public SystemCliRunner(GroupService groupService, GroupSessionCli groupSessionCli) {
        this.groupService = groupService;
        this.groupSessionCli = groupSessionCli;
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

        System.out.println("Groupok:");
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).getName());
        }
    }

    private void registerGroup(Scanner scanner) {
        System.out.print("Add meg a group nevét: ");
        String groupName = scanner.nextLine();

        System.out.print("Add meg az access code-ot: ");
        String accessCode = scanner.nextLine();

        System.out.print("Add meg az admin felhasználó nevét: ");
        String adminUserName = scanner.nextLine();

        UserGroup createdGroup = groupService.registerGroup(groupName, accessCode, adminUserName);
        System.out.println("Group létrehozva: " + createdGroup.getName());
        System.out.println("Az admin felhasználó automatikusan létrejött: " + adminUserName);
    }

    private void enterGroup(Scanner scanner) {
        List<UserGroup> groups = groupService.findAllGroups();

        if (groups.isEmpty()) {
            System.out.println("Nincs még regisztrált group.");
            return;
        }

        System.out.println("Groupok:");
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).getName());
        }

        System.out.print("Add meg a kiválasztott group sorszámát: ");
        String inputIndex = scanner.nextLine();

        try {
            int selectedIndex = Integer.parseInt(inputIndex);

            if (selectedIndex < 1 || selectedIndex > groups.size()) {
                System.out.println("Érvénytelen sorszám.");
                return;
            }

            UserGroup selectedGroup = groups.get(selectedIndex - 1);

            System.out.print("Add meg az access code-ot: ");
            String accessCode = scanner.nextLine();

            boolean valid = groupService.validateAccessCode(selectedGroup.getId(), accessCode);
            if (!valid) {
                System.out.println("Hibás access code.");
                return;
            }

            groupSessionCli.openGroupSession(scanner, selectedGroup);

        } catch (NumberFormatException e) {
            System.out.println("Kérlek számot adj meg.");
        }
    }
}