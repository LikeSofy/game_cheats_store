package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.dto.SaleCreationRequest;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.service.ProductService;
import by.sofy.game_cheats_store.service.SaleService;
import by.sofy.game_cheats_store.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequiredArgsConstructor
public class SalesController extends BaseController{
    private final SaleService saleService;
    private final ProductService productService;
    private final UserService userService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin/sales")
    public String returnAdminViewPage(Model model) {
        model.addAttribute("sales", saleService.findAll());
        return "sales-list-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin/sales/create")
    public String returnCreatePage(Model model) {
        model.addAttribute("request", new SaleCreationRequest());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("products", productService.findAll());
        return "sales-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping(value = "/admin/sales/create")
    public String createSale(Model model, @ModelAttribute SaleCreationRequest request) {
        try{
            saleService.save(request);
        }
        catch(BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
        }
        model.addAttribute("request", request);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("products", productService.findAll());

        return "sales-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping(value = "/admin/sales/delete/{id}")
    public RedirectView deleteProductGroup(Model model, @PathVariable Long id) {
        saleService.delete(id);
        return new RedirectView("../");
    }

    @GetMapping(value = "/sales")
    public String returnSalesPage(Model model) {
        model.addAttribute("sales", saleService.findAll());
        return "sales-list-user";
    }
}
