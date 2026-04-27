package hu.wardanger.devicemanager.service;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.entity.SmartApplication;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.repository.MenuItemRepository;
import hu.wardanger.devicemanager.repository.MenuRepository;
import hu.wardanger.devicemanager.repository.SmartApplicationRepository;
import hu.wardanger.devicemanager.repository.UserAccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class MenuManagementService {

    private final UserAccountRepository userAccountRepository;
    private final SmartApplicationRepository smartApplicationRepository;
    private final MenuItemRepository menuItemRepository;
    private final MenuRepository menuRepository;

    public MenuManagementService(UserAccountRepository userAccountRepository,
                                 SmartApplicationRepository smartApplicationRepository,
                                 MenuItemRepository menuItemRepository,
                                 MenuRepository menuRepository) {
        this.userAccountRepository = userAccountRepository;
        this.smartApplicationRepository = smartApplicationRepository;
        this.menuItemRepository = menuItemRepository;
        this.menuRepository = menuRepository;
    }

    @Transactional(readOnly = true)
    public List<SmartApplication> findAllApplications() {
        return smartApplicationRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(SmartApplication::getName))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserAccount findUserWithRootMenuItems(String userId) {
        return userAccountRepository.findDetailedById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));
    }

    @Transactional(readOnly = true)
    public List<MenuItem> findRootMenuItems(String userId) {
        UserAccount user = findUserWithRootMenuItems(userId);

        if (user.getRootMenu() == null || user.getRootMenu().getMenuItems() == null) {
            return List.of();
        }

        return user.getRootMenu().getMenuItems().stream()
                .sorted(Comparator.comparing(
                        item -> item.getPositionIndex() == null ? Integer.MAX_VALUE : item.getPositionIndex()
                ))
                .toList();
    }

    @Transactional
    public void addApplicationToRootMenu(String userId, String applicationId) {
        UserAccount user = userAccountRepository.findDetailedById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        Menu rootMenu = user.getRootMenu();
        if (rootMenu == null) {
            throw new IllegalArgumentException("A felhasználóhoz nem tartozik főmenü.");
        }

        SmartApplication application = smartApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen alkalmazás."));

        boolean alreadyExists = rootMenu.getMenuItems().stream()
                .anyMatch(item -> item.getApplication() != null
                        && item.getApplication().getId().equals(applicationId));

        if (alreadyExists) {
            throw new IllegalArgumentException("Ez az alkalmazás már benne van a főmenüben.");
        }

        int nextPosition = rootMenu.getMenuItems().size() + 1;

        MenuItem menuItem = new MenuItem();
        menuItem.setId(UUID.randomUUID().toString());
        menuItem.setName(application.getName());
        menuItem.setPositionIndex(nextPosition);
        menuItem.setMenu(rootMenu);
        menuItem.setApplication(application);

        menuItemRepository.save(menuItem);
    }

    @Transactional
    public void removeApplicationFromRootMenu(String userId, String menuItemId) {
        UserAccount user = userAccountRepository.findDetailedById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        Menu rootMenu = user.getRootMenu();
        if (rootMenu == null) {
            throw new IllegalArgumentException("A felhasználóhoz nem tartozik főmenü.");
        }

        MenuItem menuItem = rootMenu.getMenuItems().stream()
                .filter(item -> item.getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen alkalmazás a főmenüben."));

        menuItemRepository.delete(menuItem);
    }

    @Transactional(readOnly = true)
    public String launchApplicationFromRootMenu(String userId, String menuItemId) {
        UserAccount user = findUserWithRootMenuItems(userId);

        Menu rootMenu = user.getRootMenu();
        if (rootMenu == null) {
            throw new IllegalArgumentException("A felhasználóhoz nem tartozik főmenü.");
        }

        MenuItem menuItem = rootMenu.getMenuItems().stream()
                .filter(item -> item.getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen alkalmazás a főmenüben."));

        if (menuItem.getApplication() == null) {
            throw new IllegalArgumentException("A menüpont nincs alkalmazáshoz rendelve.");
        }

        String launchMessage = menuItem.getApplication().getLaunchMessage();

        if (launchMessage == null || launchMessage.isBlank()) {
            return menuItem.getApplication().getName() + " elindult!";
        }

        return launchMessage;
    }

    @Transactional(readOnly = true)
    public List<Menu> findSubMenus(String userId) {
        UserAccount user = findUserWithRootMenuItems(userId);

        if (user.getRootMenu() == null || user.getRootMenu().getChildMenus() == null) {
            return List.of();
        }

        return user.getRootMenu().getChildMenus().stream()
                .sorted(Comparator.comparing(Menu::getName))
                .toList();
    }

    @Transactional
    public void createSubMenu(String userId, String submenuName) {
        UserAccount user = userAccountRepository.findDetailedById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        Menu rootMenu = user.getRootMenu();
        if (rootMenu == null) {
            throw new IllegalArgumentException("A felhasználóhoz nem tartozik főmenü.");
        }

        boolean alreadyExists = rootMenu.getChildMenus().stream()
                .anyMatch(menu -> menu.getName().equalsIgnoreCase(submenuName));

        if (alreadyExists) {
            throw new IllegalArgumentException("Már létezik ilyen nevű almenü.");
        }

        Menu subMenu = new Menu();
        subMenu.setId(UUID.randomUUID().toString());
        subMenu.setName(submenuName);
        subMenu.setParentMenu(rootMenu);

        menuRepository.save(subMenu);
    }

    @Transactional
    public void deleteSubMenu(String userId, String submenuId) {
        UserAccount user = userAccountRepository.findDetailedById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        Menu rootMenu = user.getRootMenu();
        if (rootMenu == null) {
            throw new IllegalArgumentException("A felhasználóhoz nem tartozik főmenü.");
        }

        Menu subMenu = rootMenu.getChildMenus().stream()
                .filter(menu -> menu.getId().equals(submenuId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen almenü."));

        if (!subMenu.getMenuItems().isEmpty()) {
            menuItemRepository.deleteAll(subMenu.getMenuItems());
            subMenu.getMenuItems().clear();
        }

        menuRepository.delete(subMenu);
    }

    @Transactional(readOnly = true)
    public Menu findSubMenuWithItems(String submenuId) {
        return menuRepository.findById(submenuId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen almenü."));
    }

    @Transactional
    public void addApplicationToSubMenu(String submenuId, String applicationId) {
        Menu subMenu = menuRepository.findById(submenuId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen almenü."));

        SmartApplication application = smartApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen alkalmazás."));

        boolean alreadyExists = subMenu.getMenuItems().stream()
                .anyMatch(item -> item.getApplication() != null
                        && item.getApplication().getId().equals(applicationId));

        if (alreadyExists) {
            throw new IllegalArgumentException("Ez az alkalmazás már benne van az almenüben.");
        }

        int nextPosition = subMenu.getMenuItems().size() + 1;

        MenuItem menuItem = new MenuItem();
        menuItem.setId(UUID.randomUUID().toString());
        menuItem.setName(application.getName());
        menuItem.setPositionIndex(nextPosition);
        menuItem.setMenu(subMenu);
        menuItem.setApplication(application);

        menuItemRepository.save(menuItem);
    }

    @Transactional
    public void removeApplicationFromSubMenu(String submenuId, String menuItemId) {
        Menu subMenu = menuRepository.findById(submenuId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen almenü."));

        MenuItem menuItem = subMenu.getMenuItems().stream()
                .filter(item -> item.getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen alkalmazás az almenüben."));

        menuItemRepository.delete(menuItem);
    }

    @Transactional(readOnly = true)
    public String launchApplicationFromSubMenu(String submenuId, String menuItemId) {
        Menu subMenu = findSubMenuWithItems(submenuId);

        MenuItem menuItem = subMenu.getMenuItems().stream()
                .filter(item -> item.getId().equals(menuItemId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen alkalmazás az almenüben."));

        if (menuItem.getApplication() == null) {
            throw new IllegalArgumentException("A menüpont nincs alkalmazáshoz rendelve.");
        }

        String launchMessage = menuItem.getApplication().getLaunchMessage();

        if (launchMessage == null || launchMessage.isBlank()) {
            return menuItem.getApplication().getName() + " elindult!";
        }

        return launchMessage;
    }
}