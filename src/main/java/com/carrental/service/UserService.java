package com.carrental.service;

import com.carrental.dto.user.UserDto;
import com.carrental.exception.NotFoundException;
import com.carrental.model.Role;
import com.carrental.model.User;
import com.carrental.repository.RoleRepository;
import com.carrental.repository.UserRepository;
import com.carrental.dto.user.UpdateProfileRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User createUser(String username, String email, String password, String first, String last, String roleName) {
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found"));
        User user = User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .firstName(first)
                .lastName(last)
                .role(role)
                .active(true)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public void setActive(Long id, boolean active) {
        User user = getById(id);
        user.setActive(active);
    }

    @Transactional
    public void changeRole(Long id, String roleName) {
        User user = getById(id);
        Role role = roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("Role not found"));
        user.setRole(role);
    }

    @Transactional
    public UserDto updateProfile(User current, String firstName, String lastName, String email) {
        current.setFirstName(firstName);
        current.setLastName(lastName);
        current.setEmail(email);
        return toDto(current);
    }

    @Transactional
    public User updateProfile(User current, UpdateProfileRequest req) {
        if (req.getUsername() != null && !req.getUsername().isBlank()) {
            current.setUsername(req.getUsername());
        }
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            current.setEmail(req.getEmail());
        }
        if (req.getFirstName() != null && !req.getFirstName().isBlank()) {
            current.setFirstName(req.getFirstName());
        }
        if (req.getLastName() != null && !req.getLastName().isBlank()) {
            current.setLastName(req.getLastName());
        }
        return current;
    }

    public static UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().getRoleName())
                .active(user.getActive())
                .build();
    }
}

