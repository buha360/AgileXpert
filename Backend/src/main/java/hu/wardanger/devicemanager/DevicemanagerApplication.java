package hu.wardanger.devicemanager;

import hu.wardanger.devicemanager.startup.StartupFailureListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DevicemanagerApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(DevicemanagerApplication.class);
        application.addListeners(new StartupFailureListener());
        application.run(args);
    }
}