package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.BuyRequest;
import by.sofy.game_cheats_store.dto.ProductCreationEditRequest;
import by.sofy.game_cheats_store.entity.Product;
import by.sofy.game_cheats_store.entity.ProductsGroup;
import by.sofy.game_cheats_store.entity.User;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.ProductGroupRepository;
import by.sofy.game_cheats_store.repository.ProductRepository;
import by.sofy.game_cheats_store.repository.UserRepository;
import by.sofy.game_cheats_store.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    @Value("${upload.path}")
    private String uploadPath;

    private final ValidationService validationService;
    private final ProductRepository productRepository;
    private final ProductGroupRepository productGroupRepository;
    private final FileUtil fileUtil;

    public List<Product> findAll(){
        return productRepository.findAll();
    }

    public List<Product> findFreeFromProductsGroupProducts(){
        return productRepository.findAllByProductsGroupIsNull();
    }

    public List<Product> findFreeFromProductsGroupProducts(Long productGroupId){
        Optional<ProductsGroup> optionalProductsGroup = productGroupRepository.findById(productGroupId);

        List<Product> result = productRepository.findAllByProductsGroupIsNull();
        if (optionalProductsGroup.isPresent()){
            result.addAll(optionalProductsGroup.get().getProducts());
        }

        return result;
    }

    public Product findById(Long id){
        return productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found Product by id: %s", id)));
    }

    public ProductCreationEditRequest findRequestById(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found Product by id: %s", id)));
        ProductCreationEditRequest request = new ProductCreationEditRequest();
        request.setName(product.getName());
        request.setPrice(product.getPrice());

        return request;
    }

    public void create(ProductCreationEditRequest request) {
        validationService.checkValidation(request);

        if (request.getFile() == null || request.getFile().isEmpty()){
            throw new BadRequestException("File can't be empty.");
        }

        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        String path = fileUtil.saveFile(request.getFile());
        product.setPath(path);
        productRepository.save(product);
    }

    public void edit(Long id, ProductCreationEditRequest request){
        validationService.checkValidation(request);
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        if (request.getFile() != null){
            String path = fileUtil.saveFile(request.getFile());
            fileUtil.deleteFile(product.getPath());
            product.setPath(path);
        }
        product.setId(id);
        productRepository.save(product);
    }

    public void deleteById(Long id){
        Product product = productRepository.findById(id).orElseThrow(NotFoundException::new);
        fileUtil.deleteFile(product.getPath());
        productRepository.delete(product);
    }

    public File toFile(Product product){
        File uploadDir = new File(uploadPath);

        log.info("IN toFile - Convert entity by id: {} to file.", product);
        log.info(uploadPath + " , " + product.getPath() + " , " + new File(uploadDir.getAbsolutePath(), product.getPath()).getName());
        return new File(uploadDir.getAbsolutePath(), product.getPath());
    }
}
