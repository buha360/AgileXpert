package hu.wardanger.devicemanager.service;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.MenuItem;
import hu.wardanger.devicemanager.entity.SmartApplication;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.repository.MenuItemRepository;
import hu.wardanger.devicemanager.repository.SmartApplicationRepository;
import hu.wardanger.devicemanager.repository.UserAccountRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MenuManagementService {

    private final UserAccountRepository userAccountRepository;
    private final SmartApplicationRepository smartApplicationRepository;
    private final MenuItemRepository menuItemRepository;

    public MenuManagementService(UserAccountRepository userAccountRepository, SmartApplicationRepository smartApplicationRepository, MenuItemRepository menuItemRepository) {
        this.userAccountRepository = userAccountRepository;
        this.smartApplicationRepository = smartApplicationRepository;
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional(readOnly = true)
    public List<SmartApplication> findAllApplications() {
        return smartApplicationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public UserAccount findUserWithRootMenuItems(String userId) {
        UserAccount user = userAccountRepository.findDetailedById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        if (user.getRootMenu() != null) {
            Hibernate.initialize(user.getRootMenu().getMenuItems());
            Hibernate.initialize(user.getRootMenu().getChildMenus());
        }

        return user;
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

        Hibernate.initialize(rootMenu.getMenuItems());

        int nextPosition = rootMenu.getMenuItems().size() + 1;

        MenuItem menuItem = new MenuItem();
        menuItem.setId(UUID.randomUUID().toString());
        menuItem.setName(application.getName());
        menuItem.setPositionIndex(nextPosition);
        menuItem.setMenu(rootMenu);
        menuItem.setApplication(application);

        menuItemRepository.save(menuItem);
    }
}