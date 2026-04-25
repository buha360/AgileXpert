package hu.wardanger.devicemanager.service;

import hu.wardanger.devicemanager.entity.Menu;
import hu.wardanger.devicemanager.entity.UserAccount;
import hu.wardanger.devicemanager.entity.UserGroup;
import hu.wardanger.devicemanager.entity.UserRole;
import hu.wardanger.devicemanager.repository.MenuRepository;
import hu.wardanger.devicemanager.repository.UserAccountRepository;
import hu.wardanger.devicemanager.repository.UserGroupRepository;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final MenuRepository menuRepository;
    private final UserGroupRepository userGroupRepository;
    private final CustomizationService customizationService;

    public UserAccountService(UserAccountRepository userAccountRepository,
                              MenuRepository menuRepository,
                              UserGroupRepository userGroupRepository,
                              CustomizationService customizationService) {
        this.userAccountRepository = userAccountRepository;
        this.menuRepository = menuRepository;
        this.userGroupRepository = userGroupRepository;
        this.customizationService = customizationService;
    }

    @Transactional
    public UserAccount createUser(String groupId, String name, String password) {
        UserGroup group = userGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen group."));

        String userId = UUID.randomUUID().toString();
        String menuId = UUID.randomUUID().toString();

        Menu rootMenu = new Menu(menuId, name + " főmenüje");
        menuRepository.save(rootMenu);

        UserAccount userAccount = new UserAccount(userId, name, password, UserRole.MEMBER);
        userAccount.setGroup(group);
        userAccount.setRootMenu(rootMenu);
        userAccount.setWallpaper(customizationService.findDefaultWallpaper());
        userAccount.setTheme(customizationService.findDefaultTheme());

        return userAccountRepository.save(userAccount);
    }

    public List<UserAccount> findUsersByGroup(String groupId) {
        return userAccountRepository.findByGroupId(groupId);
    }

    public void deleteUserById(String id) {
        if (!userAccountRepository.existsById(id)) {
            throw new IllegalArgumentException("Nincs ilyen azonosítójú felhasználó.");
        }
        userAccountRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public UserAccount findDetailedUserById(String id) {
        UserAccount user = userAccountRepository.findDetailedById(id)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        if (user.getRootMenu() != null) {
            Hibernate.initialize(user.getRootMenu().getMenuItems());
            Hibernate.initialize(user.getRootMenu().getChildMenus());
        }

        return user;
    }

    @Transactional(readOnly = true)
    public UserAccount authenticateUser(String userId, String password) {
        UserAccount user = userAccountRepository.findDetailedById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Nincs ilyen felhasználó."));

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("Hibás jelszó.");
        }

        if (user.getRootMenu() != null) {
            Hibernate.initialize(user.getRootMenu().getMenuItems());
            Hibernate.initialize(user.getRootMenu().getChildMenus());
        }

        return user;
    }
}