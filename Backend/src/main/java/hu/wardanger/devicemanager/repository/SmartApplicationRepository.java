package hu.wardanger.devicemanager.repository;

import hu.wardanger.devicemanager.entity.SmartApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SmartApplicationRepository extends JpaRepository<SmartApplication, String> {
}