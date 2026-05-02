package com.example.hhflow.security;

import com.example.hhflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = username.trim();
        return userRepository.findByEmail(email)
                .map(CustomUserDetails::fromUser)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
