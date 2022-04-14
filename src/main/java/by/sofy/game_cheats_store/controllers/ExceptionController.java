package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.exceptions.CustomAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.view.RedirectView;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(CustomAuthenticationException.class)
    public RedirectView handleNotFoundException(CustomAuthenticationException e) {
        return new RedirectView("/login");
    }
}
