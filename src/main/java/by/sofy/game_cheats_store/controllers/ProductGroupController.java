package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.dto.BuyRequest;
import by.sofy.game_cheats_store.dto.ProductGroupEditCreationRequest;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ProductGroupController extends BaseController{
    private final ProductService productsService;
    private final ProductGroupService productGroupService;
    private final GameService gameService;
    private final CategoryService categoryService;
    private final SaleService saleService;

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/product-groups")
    public String returnAdminViewPage(Model model) {
        model.addAttribute("groups", productGroupService.findAll());
        return "product-groups-list-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/product-groups/create")
    public String returnCreatePage(Model model) {
        model.addAttribute("request", new ProductGroupEditCreationRequest());
        model.addAttribute("games", gameService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("products", productsService.findFreeFromProductsGroupProducts());
        return "product-group-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/product-groups/create")
    public String createProductGroup(Model model, @ModelAttribute ProductGroupEditCreationRequest request) {
        model.addAttribute("request", request);
        model.addAttribute("games", gameService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("products", productsService.findFreeFromProductsGroupProducts());

        try {
            productGroupService.save(request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            return "product-group-create-admin";
        }
        model.addAttribute("info", "product created");
        return "product-group-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/product-groups/edit/{id}")
    public String returnEditPage(Model model, @PathVariable Long id) {
        model.addAttribute("request", productGroupService.findRequest(id));
        model.addAttribute("games", gameService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("products", productsService.findFreeFromProductsGroupProducts(id));

        return "product-group-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/product-groups/edit/{id}")
    public String changeProductGroup(Model model, @PathVariable Long id, @ModelAttribute ProductGroupEditCreationRequest request) {
        model.addAttribute("request", request);
        model.addAttribute("games", gameService.findAll());
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("products", productsService.findFreeFromProductsGroupProducts(id));

        try {
            productGroupService.save(id, request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            return "product-group-create-admin";
        }
        model.addAttribute("info", "product created");
        return "product-group-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/product-groups")
    public String deleteProductGroup(Model model, @RequestParam Long deleteId) {
        try{
            productGroupService.delete(deleteId);
        }
        catch (DataIntegrityViolationException exception){
            model.addAttribute("error", "Product group used by other entity");
        }
        model.addAttribute("groups", productGroupService.findAll());
        return "product-groups-list-admin";
    }

    @GetMapping(value = "/product-groups/")
    public String returnProductGroupsListPage(Model model, @RequestParam Long gameId) {
        model.addAttribute("productsGroups", productGroupService.findAllByGameId(gameId));
        return "products-groups";
    }

    @GetMapping(value = "/product-groups/{id}")
    public String returnProductGroupPage(Model model, @PathVariable Long id) {
        model.addAttribute("productsGroup", productGroupService.findById(id));
        model.addAttribute("request", new BuyRequest());
        return "products-group";
    }

    @PostMapping(value = "/product-groups/{id}")
    public String buyProduct(Model model, @PathVariable Long id, Principal principal, @ModelAttribute BuyRequest request) {
        try{
            saleService.buyProductByUser(principal, request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
        }
        model.addAttribute("productsGroup", productGroupService.findById(id));
        model.addAttribute("request", request);
        return "products-group";
    }
}
