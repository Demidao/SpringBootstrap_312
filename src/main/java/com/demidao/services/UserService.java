package com.demidao.services;

import com.demidao.models.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface UserService extends UserDetailsService {

    List<User> index();

    User show(long id);

    User findByEmail(String email);

    void save(User user, Collection<String> roles);

    void update(User newUser, Collection<String> newRoles);

    void delete(long id);
}
