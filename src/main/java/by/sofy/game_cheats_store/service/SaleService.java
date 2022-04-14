package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.BuyRequest;
import by.sofy.game_cheats_store.dto.SaleCreationRequest;
import by.sofy.game_cheats_store.entity.Product;
import by.sofy.game_cheats_store.entity.Sale;
import by.sofy.game_cheats_store.entity.User;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.CustomAuthenticationException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.ProductRepository;
import by.sofy.game_cheats_store.repository.SaleRepository;
import by.sofy.game_cheats_store.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaleService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public List<Sale> findAll() {
        return saleRepository.findAll();
    }

    public void save(SaleCreationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BadRequestException(String.format("Not found user by id: %s", request.getUserId())));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BadRequestException(String.format("Not found product by id: %s", request.getProductId())));

        if (saleRepository.findByUserAndProduct(user, product).isPresent()) {
            throw new BadRequestException("User already buy this product");
        }

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setUser(user);

        saleRepository.save(sale);
    }

    public void buyProductByUser(Principal principal, BuyRequest request) {
        if (principal == null){
            throw new CustomAuthenticationException("Invalid authorize");
        }

        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new CustomAuthenticationException("Invalid authorize"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BadRequestException(String.format("Not found product by id: %s", request.getProductId())));

        if (saleRepository.findByUserAndProduct(user, product).isPresent()) {
            throw new BadRequestException("User already buy this product");
        }

        if (user.getBalance().compareTo(product.getPrice()) < 0) {
            throw new BadRequestException("Insufficient funds");
        }

        Sale sale = new Sale();
        sale.setProduct(product);
        sale.setUser(user);

        user.setBalance(user.getBalance().subtract(product.getPrice()));

        saleRepository.save(sale);
        userRepository.save(user);
    }

    public void delete(Long id) {
        saleRepository.deleteById(id);
    }

    public Sale findByPrincipalAndProductId(Principal principal, Long productId) {
        if (principal == null){
            throw new CustomAuthenticationException();
        }

        User user = userRepository.findByUsername(principal.getName()).orElseThrow(CustomAuthenticationException::new);
        Sale sale = saleRepository.findByUserAndProduct_Id(user, productId).orElseThrow(NotFoundException::new);

        return sale;
    }
}
