package hu.wardanger.devicemanager.repository;

import hu.wardanger.devicemanager.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, String> {
}