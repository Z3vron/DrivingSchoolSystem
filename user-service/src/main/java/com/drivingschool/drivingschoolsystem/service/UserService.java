package com.drivingschool.drivingschoolsystem.service;

import com.drivingschool.drivingschoolsystem.exception.DuplicateEmailException;
import com.drivingschool.drivingschoolsystem.exception.ResourceNotFoundException;
import com.drivingschool.drivingschoolsystem.model.User;
import com.drivingschool.drivingschoolsystem.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already exists");
        }
        User saved = userRepository.save(user);
        logger.info("User created id={} role={}", saved.getId(), saved.getRole());
        return saved;
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found");
        }
        userRepository.deleteById(id);
        logger.info("User deleted id={}", id);
    }
}
