package hu.wardanger.devicemanager.startup;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

public class StartupFailureListener implements ApplicationListener<ApplicationFailedEvent> {

    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        Throwable rootCause = getRootCause(event.getException());

        System.out.println();
        System.out.println("Indítási ellenörzés sikertelen.");

        if (rootCause != null && rootCause.getMessage() != null) {
            System.out.println("Ok: " + rootCause.getMessage());
            System.out.println("------------------------------");
        } else {
            System.out.println("Ok: Ismeretlen indítási hiba.");
            System.out.println("------------------------------");
        }
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable current = throwable;

        while (current != null && current.getCause() != null) {
            current = current.getCause();
        }

        return current;
    }
}