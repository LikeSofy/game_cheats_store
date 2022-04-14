package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.ProductGroupEditCreationRequest;
import by.sofy.game_cheats_store.entity.ProductsGroup;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.CategoryRepository;
import by.sofy.game_cheats_store.repository.GameRepository;
import by.sofy.game_cheats_store.repository.ProductGroupRepository;
import by.sofy.game_cheats_store.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductGroupService {
    private final ProductGroupRepository productGroupRepository;
    private final GameRepository gameRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;


    public List<ProductsGroup> findAll() {
        List<ProductsGroup> all = productGroupRepository.findAll();

        log.info("In findAll - return: {}", all);
        return all;
    }

    public ProductsGroup findById(Long id) {
        ProductsGroup entity = productGroupRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found ProductGroup by id: %s", id)));

        log.info("In findById - return: {}", entity);
        return entity;
    }

    public List<ProductsGroup> findAllByGameId(Long gameId) {
        return productGroupRepository.findAllByGame_Id(gameId);
    }

    public void save(ProductGroupEditCreationRequest request) {
        ProductsGroup productsGroup = new ProductsGroup();
        productsGroup.setName(request.getName());
        productsGroup.setDescription(request.getDescription());
        productsGroup.setGame(gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new BadRequestException("Invalid game")));
        productsGroup.setCategory(categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Invalid category")));
        productsGroup.setProducts(productRepository.findAllById(request.getProductsIds()));

        log.info("In save - save new group: {}", productsGroup);
        productGroupRepository.save(productsGroup);
    }

    public void save(Long id, ProductGroupEditCreationRequest request) {
        ProductsGroup productsGroup = productGroupRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Invalid product group"));

        productsGroup.setId(id);
        productsGroup.setName(request.getName());
        productsGroup.setDescription(request.getDescription());
        productsGroup.setGame(gameRepository.findById(request.getGameId())
                .orElseThrow(() -> new BadRequestException("Invalid game")));
        productsGroup.setCategory(categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new BadRequestException("Invalid category")));
        productsGroup.setProducts(productRepository.findAllById(request.getProductsIds()));

        productGroupRepository.save(productsGroup);
    }


    public ProductGroupEditCreationRequest findRequest(Long id) {
        ProductsGroup productsGroup = productGroupRepository.findById(id).orElseThrow(() ->
                new BadRequestException("Product group not found"));
        ProductGroupEditCreationRequest request = new ProductGroupEditCreationRequest();
        request.setName(productsGroup.getName());
        request.setDescription(productsGroup.getDescription());
        request.setGameId(productsGroup.getGame().getId());
        request.setCategoryId(productsGroup.getCategory().getId());
        request.setProductsIds(productsGroup.getProducts()
                .stream().map(product -> product.getId())
                .collect(Collectors.toList()));
        return request;
    }

    public void delete(Long id) {
        productGroupRepository.deleteById(id);
    }
}
