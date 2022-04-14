package by.sofy.game_cheats_store.controllers;

import by.sofy.game_cheats_store.dto.ProductCreationEditRequest;
import by.sofy.game_cheats_store.entity.Sale;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.service.ProductService;
import by.sofy.game_cheats_store.service.SaleService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ProductsController extends BaseController{
    private final ProductService productsService;
    private final SaleService saleService;

    @GetMapping(value = "/admin/products")
    public String returnAdminViewPage(Model model) {
        model.addAttribute("products", productsService.findAll());

        return "products-list-admin";
    }

    @GetMapping(value = "/admin/products/create")
    public String returnCreatePage(Model model) {
        model.addAttribute("request", new ProductCreationEditRequest());

        return "products-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/products/create")
    public String createCategory(Model model, @ModelAttribute ProductCreationEditRequest request) {
        model.addAttribute("request", request);

        try {
            productsService.create(request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            return "products-create-admin";
        }
        model.addAttribute("info", "product created");
        return "products-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @GetMapping(value = "/admin/products/edit/{id}")
    public String returnEditPage(Model model, @PathVariable Long id) {
        model.addAttribute("request", productsService.findRequestById(id));

        return "products-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/products/edit/{id}")
    public String changeProduct(Model model, @PathVariable Long id, @ModelAttribute ProductCreationEditRequest request) {
        model.addAttribute("request", request);

        try {
            productsService.edit(id, request);
        }
        catch (BadRequestException exception){
            model.addAttribute("error", exception.getMessage());
            return "products-create-admin";
        }
        model.addAttribute("info", "product changed");
        return "products-create-admin";
    }

    @PreAuthorize("hasAnyRole('ROLE_MANAGER', 'ROLE_ADMIN')")
    @PostMapping(value = "/admin/products")
    public String deleteProduct(Model model, @RequestParam Long deleteId) {
        try{
            productsService.deleteById(deleteId);
        }
        catch (DataIntegrityViolationException exception){
            model.addAttribute("error", "Product used by other entity");
        }
        model.addAttribute("products", productsService.findAll());
        return "products-list-admin";
    }

    @SneakyThrows
    @GetMapping(value = "/products/download/{id}")
    public ResponseEntity<byte[]> returnAdminViewPage(Principal principal, @PathVariable Long id) {
        Sale sale = saleService.findByPrincipalAndProductId(principal, id);
        File file = productsService.toFile(sale.getProduct());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        httpHeaders.set(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment().filename(file.getName()).build().toString());

        byte[] fileBytes = Files.readAllBytes(file.toPath());

        return new ResponseEntity<>(fileBytes, httpHeaders, HttpStatus.OK);
    }
}
