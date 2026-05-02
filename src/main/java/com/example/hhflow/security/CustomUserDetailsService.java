package com.example.hhflow.security;

import com.example.hhflow.model.UserAccount;
import com.example.hhflow.repository.EmployerRepository;
import com.example.hhflow.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;
    private final EmployerRepository employerRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String key = username.trim();

        return userAccountRepository.findByPhone(key)
                .map(CustomUserDetails::fromUserAccount)
                .orElseGet(() -> loadByEmployerEmail(key));
    }

    private CustomUserDetails loadByEmployerEmail(String email) {
        return employerRepository.findByEmail(email)
                .flatMap(e -> userAccountRepository.findByEmployer_Id(e.getId()))
                .map(CustomUserDetails::fromUserAccount)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }
}
