package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.dto.RefillRequest;
import by.sofy.game_cheats_store.dto.RegistrationRequest;
import by.sofy.game_cheats_store.dto.UserEditRequest;
import by.sofy.game_cheats_store.entity.UserRoles;
import by.sofy.game_cheats_store.exceptions.CustomAuthenticationException;
import by.sofy.game_cheats_store.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
@Slf4j
public class UserController extends BaseController{
    private final UserService userService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/admin/users")
    public String returnAdminViewPage(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "users-list-admin";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/admin/users/create")
    public String returnCreatePage(Model model) {
        model.addAttribute("request", new UserEditRequest());
        model.addAttribute("roles", Arrays.stream(UserRoles.values()).map(Enum::name).toArray());
        return "user-edit-admin";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/admin/users/create")
    public String createUser(Model model, @ModelAttribute UserEditRequest request) {
        model.addAttribute("request", request);
        model.addAttribute("roles", Arrays.stream(UserRoles.values()).map(Enum::name).toArray());
        try {
            userService.register(request);
        }
        catch (Exception ex){
            model.addAttribute("message", ex.getMessage());
            return "registration";
        }
        model.addAttribute("message", "User created");
        return "user-edit-admin";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(value = "/admin/users/edit/{id}")
    public String returnEditPage(Model model, @PathVariable Long id) {
        UserEditRequest request = userService.findRequestById(id);
        model.addAttribute("id", id);
        model.addAttribute("roles", Arrays.stream(UserRoles.values()).map(Enum::name).toArray());
        model.addAttribute("request", request);
        return "user-edit-admin";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping(value = "/admin/users/edit/{id}")
    public String editUser(Model model, @PathVariable Long id, @RequestPart UserEditRequest request) {
        model.addAttribute("id", id);
        model.addAttribute("roles", Arrays.stream(UserRoles.values()).map(Enum::name).toArray());
        try {
            userService.edit(id, request);
        }
        catch (Exception ex){
            model.addAttribute("message", ex.getMessage());
            return "user-edit-admin";
        }
        model.addAttribute("message", "User registered");
        return "user-edit-admin";
    }

    @GetMapping(value = "/registration")
    public String registerPage(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "registration";
    }

    @PostMapping(value = "/registration")
    public String registerData(Model model, @ModelAttribute RegistrationRequest registrationRequest) {
        try {
            userService.register(registrationRequest);
        }
        catch (Exception ex){
            model.addAttribute("message", ex.getMessage());
            return "registration";
        }
        model.addAttribute("message", "User registered");
        return "registration";
    }

    @GetMapping(value = "/login")
    public String returnLoginPage(Model model) {
        model.addAttribute("loginRequest", new RegistrationRequest());
        return "login";
    }

    @GetMapping(value = "/refill")
    public String returnRefillPage(Model model, Principal principal){
        if (principal == null){
            throw new CustomAuthenticationException();
        }
        model.addAttribute("request", new RefillRequest());
        return "payment";
    }

    @PostMapping(value = "/refill")
    public String refill(Model model, Principal principal, @ModelAttribute RefillRequest request){
        userService.refill(principal, request);
        model.addAttribute("request", request);
        return "redirect:";
    }

}
