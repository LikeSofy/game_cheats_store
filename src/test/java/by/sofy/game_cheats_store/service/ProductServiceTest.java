package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.CategoryCreationEditRequest;
import by.sofy.game_cheats_store.dto.ProductCreationEditRequest;
import by.sofy.game_cheats_store.entity.Category;
import by.sofy.game_cheats_store.entity.Product;
import by.sofy.game_cheats_store.entity.ProductsGroup;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.ProductRepository;
import by.sofy.game_cheats_store.utils.FileUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    @Mock
    ProductService productService;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    FileUtil fileUtil;

    @Test
    void findAll() {
        List<Product> expected = new LinkedList<>();
        Mockito.doReturn(expected).when(productRepository).findAll();
        List<Product> actual = productService.findAll();

        assertSame(expected, actual);
    }

    @Test
    void findById__idInDb__returnProductGroup() {
        Product expected = new Product();
        Mockito.doReturn(Optional.of(expected)).when(productRepository).findById(0L);
        Product actual = productService.findById(0L);

        assertSame(expected, actual);
    }

    @Test
    void findById__idNotInDb__throwBadRequestException() {

        String expected = "Not found Product by id: 0";

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.findById(0L);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void findRequestById__idNotInDb__throwNotFoundException(){
        String expected = "Not found Product by id: 0";

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productService.findRequestById(0L);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void findRequestById__idInDb__returnRequest(){
        Product product = new Product();
        product.setName("name");
        product.setPrice(BigDecimal.ONE);

        Mockito.doReturn(Optional.of(product)).when(productRepository).findById(0L);

        ProductCreationEditRequest expected = new ProductCreationEditRequest();
        expected.setName(product.getName());
        expected.setPrice(product.getPrice());

                ProductCreationEditRequest actual = productService.findRequestById(0L);

        assertEquals(expected, actual);
    }

    static Stream<Arguments> createDataProvider() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(false).when(multipartFile).isEmpty();
        MultipartFile emptyMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(true).when(emptyMultipartFile).isEmpty();
        return Stream.of(
                Arguments.of(new ProductCreationEditRequest("", BigDecimal.valueOf(1), multipartFile)
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new ProductCreationEditRequest("invalidNameLengthMoreThan30********************************************", BigDecimal.valueOf(1), multipartFile)
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new ProductCreationEditRequest("validName", BigDecimal.valueOf(-1), multipartFile)
                        , "Price can't be less than 0."),
                Arguments.of(new ProductCreationEditRequest("validName", BigDecimal.valueOf(1), null)
                        , "File can't be empty."),
                Arguments.of(new ProductCreationEditRequest("validName", BigDecimal.valueOf(1), emptyMultipartFile)
                        , "File can't be empty.")

        );
    }

    @ParameterizedTest
    @MethodSource("createDataProvider")
    void create__invalidRequest__throwBadRequestException(ProductCreationEditRequest request, String expected) {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productService.create(request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void create__validRequest__saveToDb() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        ProductCreationEditRequest request = new ProductCreationEditRequest("name", BigDecimal.valueOf(1), multipartFile);

        String path = "path";

        Product excepted = new Product();
        excepted.setName(request.getName());
        excepted.setPrice(request.getPrice());
        excepted.setPath(path);

        Mockito.doReturn(path).when(fileUtil).saveFile(multipartFile);

        productService.create(request);

        ArgumentCaptor<Product> categoryCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository, Mockito.times(1)).save(categoryCaptor.capture());

        Product actual = categoryCaptor.getValue();

        assertEquals(excepted, actual);
    }

    static Stream<Arguments> editDataProvider() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(false).when(multipartFile).isEmpty();
        MultipartFile emptyMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(true).when(emptyMultipartFile).isEmpty();
        return Stream.of(
                Arguments.of(new ProductCreationEditRequest("", BigDecimal.valueOf(1), multipartFile)
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new ProductCreationEditRequest("invalidNameLengthMoreThan30********************************************", BigDecimal.valueOf(1), multipartFile)
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new ProductCreationEditRequest("validName", BigDecimal.valueOf(-1), multipartFile)
                        , "Price can't be less than 0.")
        );
    }

    @ParameterizedTest
    @MethodSource("editDataProvider")
    void edit__invalidRequest__throwBadRequestException(ProductCreationEditRequest request, String expected) {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productService.edit(0L, request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void edit__validRequest__saveToDb() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        ProductCreationEditRequest request = new ProductCreationEditRequest("name", BigDecimal.valueOf(1), multipartFile);

        String path = "path";

        Product excepted = new Product();
        excepted.setId(0L);
        excepted.setName(request.getName());
        excepted.setPrice(request.getPrice());
        excepted.setPath(path);

        Mockito.doReturn(path).when(fileUtil).saveFile(multipartFile);

        productService.edit(0L, request);

        ArgumentCaptor<Product> categoryCaptor = ArgumentCaptor.forClass(Product.class);
        Mockito.verify(productRepository, Mockito.times(1)).save(categoryCaptor.capture());

        Product actual = categoryCaptor.getValue();

        assertEquals(excepted, actual);
    }

    @Test
    void toFile(){
        String excepted = "example.file";
        Product product = new Product();
        product.setPath(excepted);
        File file = productService.toFile(product);

        String actual = file.getName();

        assertEquals(excepted, actual);
    }
}