package com.drivingschool.drivingschoolsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.drivingschool.drivingschoolsystem.model.User;
import com.drivingschool.drivingschoolsystem.model.User.Role;
import com.drivingschool.drivingschoolsystem.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DrivingSchoolSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DrivingSchoolSystemApplication.class, args);
    }
    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            userRepository.save(new User(null, "Imie1", "Nazwisko1", "email1@example.com", null, Role.TRAINEE));
            userRepository.save(new User(null, "Imie2", "Nazwisko2", "email2@example.com", null, Role.INSTRUCTOR));
        };
    }

}

