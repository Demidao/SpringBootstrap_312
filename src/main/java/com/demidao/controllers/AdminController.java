package com.demidao.controllers;

import com.demidao.models.User;
import com.demidao.services.RoleService;
import com.demidao.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }


    @GetMapping()
    public String index(Model model, Principal principal) {
        model.addAttribute("userList", userService.index());
        model.addAttribute("allRoles", roleService.findAll());
        model.addAttribute("signedUser", userService.loadUserByUsername(principal.getName()));
        model.addAttribute("newUser", new User());

        return "admin";
    }

    @PostMapping("/tab-pane-newuser")
    public String create(@ModelAttribute("newUser") User user,
                         BindingResult bindingResult,
                         @RequestParam(name = "newRoles") List<String> allRoles) {
        if (bindingResult.hasErrors()) {
            return "fragments/tab-pane-newuser";
        }
        userService.save(user, allRoles);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") long id) {
        model.addAttribute("user", userService.show(id));
        return "edit";
    }

    @PostMapping("/{id}")
    public String update(@ModelAttribute("userInModal") User user,
                         BindingResult bindingResult,
                         @RequestParam(name = "newRoles") List<String> newRoles) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        userService.update(user, newRoles);
        return "redirect:/admin";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}
