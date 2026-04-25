package hu.wardanger.devicemanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI deviceManagerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Device Manager API")
                        .version("1.0.0")
                        .description("REST API for managing groups, users, menus, applications, wallpapers and themes in the Device Manager system.")
                        .contact(new Contact()
                                .name("BM")));
    }
}