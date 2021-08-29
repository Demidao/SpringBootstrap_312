package com.demidao.services;

import com.demidao.models.Role;
import com.demidao.models.User;
import com.demidao.repos.RoleRepository;
import com.demidao.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new UsernameNotFoundException("No user with email " + email);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(),
                user.getPassword(), user.getRoles());
    }

    /*
    Инициализация рыбных данных
    Пароль для всех 0
     */
    private void createInitData() {
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleUser = new Role("ROLE_USER");

        roleRepository.save(roleAdmin);
        roleRepository.save(roleUser);

        List<Role> adminRoles = Collections.singletonList(roleRepository.findByRole("ROLE_ADMIN"));
        List<Role> userRoles = Collections.singletonList(roleRepository.findByRole("ROLE_USER"));

        User adminUser = new User("Charlie", "Croker", 35,
                "admin@admin.ru",
                "$2y$12$TilEelQUr/VsKh8QVZ9Ocef8vYJBOUxvKD.HFX9./hqaQphXU9yOS"); // пароль 0
        User user1 = new User("Stella", "Bridger", 30,
                "stella_bri@neveremail.it",
                "$2y$12$TilEelQUr/VsKh8QVZ9Ocef8vYJBOUxvKD.HFX9./hqaQphXU9yOS"); // пароль 0
        User user2 = new User("Steve", "Frazelli", 38,
                "frazza@neveremail.it",
                "$2y$12$TilEelQUr/VsKh8QVZ9Ocef8vYJBOUxvKD.HFX9./hqaQphXU9yOS"); // пароль 0

        adminUser.setRoles(adminRoles);
        user1.setRoles(userRoles);
        user2.setRoles(userRoles);

        userRepository.save(adminUser);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Override
    public List<User> index() {
        if (userRepository.findAll().isEmpty()) {
            createInitData();
        }
        return userRepository.findAll();
    }

    @Override
    public User show(long id) {
        return userRepository.getById(id);
    }

    @Override
    public User findByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("No user with email " + email);
        }
        return user;
    }

    @Override
    public void save(User user, Collection<String> roles) {
        List<Role> newUserRoles = new ArrayList<>();
        if (!roles.isEmpty()) {
            for (String role : roles) {
                newUserRoles.add(roleRepository.findByRole(role));
            }
        } else {
            List<Role> userRoles = Collections.singletonList(roleRepository.findByRole("ROLE_USER"));
            user.setRoles(userRoles);
        }
        user.setRoles(newUserRoles);
        user.setPassword(passEncoder(user.getPassword()));
        userRepository.save(user);
    }

    @Override
    public void update(User newUser, Collection<String> newRoles) {
        if (!checkPasswords(userRepository.findById(newUser.getUser_id()), newUser)) {
            newUser.setPassword(passEncoder(newUser.getPassword()));
        }

        if (newRoles.isEmpty()) {
            newUser.setRoles(userRepository.findById(newUser.getUser_id()).getRoles());
        } else {
            List<Role> updatedRoles = new ArrayList<>();
            for (String role : newRoles) {
                Role newRole = roleRepository.findByRole(role);
                updatedRoles.add(newRole);
            }
            newUser.setRoles(updatedRoles);
        }
        userRepository.save(newUser);
    }

    @Override
    public void delete(long id) {
        User user = show(id);
        if (user == null) {
            throw new NullPointerException("User with id=" + id + " not found");
        }
        userRepository.delete(user);
    }

    private String passEncoder(String password) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        return passwordEncoder.encode(password);
    }

    private boolean checkPasswords(User u1, User u2) {
        return u1.getPassword().equals(u2.getPassword());
    }
}
