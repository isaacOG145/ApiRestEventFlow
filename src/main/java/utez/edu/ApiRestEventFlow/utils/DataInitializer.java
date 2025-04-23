package utez.edu.ApiRestEventFlow.utils;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import utez.edu.ApiRestEventFlow.Role.Role; // Importa tu enum Role
import utez.edu.ApiRestEventFlow.user.model.User;
import utez.edu.ApiRestEventFlow.user.model.UserRepository;

import java.util.Optional;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {

            // Crear un usuario SUPERADMIN si no existe
            Optional<User> optionalSuperAdmin = userRepository.findByEmail("superadmin@example.com");
            if (!optionalSuperAdmin.isPresent()) {
                User superAdmin = new User();
                superAdmin.setName("Super Admin");
                superAdmin.setEmail("superadmin@example.com");
                superAdmin.setPhone("7772002020");
                superAdmin.setCompany("Utez");
                superAdmin.setPassword(passwordEncoder.encode("ultraSecret"));
                superAdmin.setRole(Role.ADMIN);
                superAdmin.setStatus(true);
                userRepository.saveAndFlush(superAdmin);
            }

        };
    }
}