package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.math.BigDecimal;
import java.security.Principal;

@Controller
@Slf4j
public class BaseController {

    @Autowired
    private UserService userService;

    @ModelAttribute("balance")
    public BigDecimal getBalance(Principal principal){
        return userService.findBalanceForCurrentUser(principal);
    }

    @ModelAttribute("principal")
    public Principal getPrincipal(Principal principal){
        return principal;
    }

}
