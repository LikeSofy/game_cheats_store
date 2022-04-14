package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.dto.CategoryCreationEditRequest;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.service.CategoryService;
import by.sofy.game_cheats_store.service.GameService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class CategoryController extends BaseController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/categories")
    public String returnAdminViewPage(Model model) {
        model.addAttribute("categories", categoryService.findAll());

        return "category-list-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/categories/create")
    public String returnCreatePage(Model model) {
        model.addAttribute("request", new CategoryCreationEditRequest());

        return "category-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/categories/create")
    public String createCategory(Model model, @ModelAttribute CategoryCreationEditRequest request) {
        try {
            categoryService.create(request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("request", request);
            return "category-create-admin";
        }
        model.addAttribute("request", request);
        model.addAttribute("info", "category created");

        return "category-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/categories/edit/{id}")
    public String returnEditPage(Model model, @PathVariable Long id) {
        model.addAttribute("request", categoryService.findRequestById(id));

        return "category-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/categories/edit/{id}")
    public String changeCategory(Model model, @PathVariable Long id, @ModelAttribute CategoryCreationEditRequest request) {
        categoryService.edit(id, request);

        try {
            categoryService.edit(id, request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("request", request);
            return "category-create-admin";
        }
        model.addAttribute("request", request);
        model.addAttribute("info", "category changed");

        return "category-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/categories")
    public String deleteCategory(Model model, @RequestParam Long deleteId) {
        try{
            categoryService.deleteById(deleteId);
        }
        catch (DataIntegrityViolationException exception){
            model.addAttribute("error", "Category used by other entity");
        }
        model.addAttribute("categories", categoryService.findAll());
        return "category-list-admin";
    }
}
