package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.dto.GameCreationEditRequest;
import by.sofy.game_cheats_store.dto.UserEditRequest;
import by.sofy.game_cheats_store.entity.UserRoles;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.service.GameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;

@Controller
@RequiredArgsConstructor
public class GamesController extends BaseController {

    private final GameService gameService;

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/games")
    public String returnAdminViewPage(Model model) {
        model.addAttribute("games", gameService.findAll());
        return "games-list-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/games/create")
    public String returnCreatePage(Model model) {
        model.addAttribute("request", new GameCreationEditRequest());
        return "game-edit-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/games/create")
    public String CreateGame(Model model, @ModelAttribute GameCreationEditRequest request) {
        try {
            gameService.create(request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("request", request);
            return "game-edit-admin";
        }
        model.addAttribute("info", "game created");
        model.addAttribute("request", request);
        return "game-edit-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/games/edit/{id}")
    public String returnEditPage(Model model, @PathVariable Long id) {
        model.addAttribute("request", gameService.findRequestById(id));
        return "game-edit-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/games/edit/{id}")
    public String EditGame(Model model, @PathVariable Long id, @ModelAttribute GameCreationEditRequest request) {
        try {
            gameService.edit(id, request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("request", request);
            return "game-edit-admin";
        }
        model.addAttribute("info", "game changed");
        model.addAttribute("request", request);
        return "game-edit-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/games")
    public String deleteGame(Model model, @RequestParam Long deleteId) {
        try{
            gameService.deleteById(deleteId);
        }
        catch (DataIntegrityViolationException exception){
            model.addAttribute("error", "Game used by other entity");
        }
        model.addAttribute("games", gameService.findAll());
        return "games-list-admin";
    }


    @GetMapping(value = "/")
    public String returnViewPage(Model model) {
        model.addAttribute("games", gameService.findAll());
        return "index";
    }

}
