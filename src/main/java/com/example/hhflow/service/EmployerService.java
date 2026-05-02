package com.example.hhflow.service;

import com.example.hhflow.exception.NotFoundException;
import com.example.hhflow.model.Role;
import com.example.hhflow.model.User;
import com.example.hhflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployerService {

    private final UserRepository userRepository;

    public User getEmployerUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employer not found: " + id));
        if (user.getRole() != Role.EMPLOYER) {
            throw new NotFoundException("Employer not found: " + id);
        }
        return user;
    }
}
