package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.ProductCreationEditRequest;
import by.sofy.game_cheats_store.dto.ProductGroupEditCreationRequest;
import by.sofy.game_cheats_store.entity.*;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.CategoryRepository;
import by.sofy.game_cheats_store.repository.GameRepository;
import by.sofy.game_cheats_store.repository.ProductGroupRepository;
import by.sofy.game_cheats_store.repository.ProductRepository;
import by.sofy.game_cheats_store.utils.FileUtil;
import org.junit.Assert;
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

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductGroupServiceTest {

    @Autowired
    @Mock
    ProductGroupService productGroupService;

    @MockBean
    ProductGroupRepository productGroupRepository;

    @MockBean
    CategoryRepository categoryRepository;

    @MockBean
    GameRepository gameRepository;

    @MockBean
    ProductRepository productRepository;

    @Test
    void findAll() {
        List<ProductsGroup> expected = new LinkedList<>();
        Mockito.doReturn(expected).when(productGroupRepository).findAll();
        List<ProductsGroup> actual = productGroupService.findAll();

        assertSame(expected, actual);
    }

    @Test
    void findById__idInDb__returnProductGroup() {
        ProductsGroup expected = new ProductsGroup();
        Mockito.doReturn(Optional.of(expected)).when(productGroupRepository).findById(0L);
        ProductsGroup actual = productGroupService.findById(0L);

        assertSame(expected, actual);
    }

    @Test
    void findById__idNotInDb__throwBadRequestException() {

        String expected = "Not found ProductGroup by id: 0";

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            productGroupService.findById(0L);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    static Stream<Arguments> saveDataProvider() {
        return Stream.of(
                Arguments.of(null, new Category(), "Invalid game"),
                Arguments.of(new Game(), null, "Invalid category")
        );
    }

    @ParameterizedTest
    @MethodSource("saveDataProvider")
    void save__incorrectRequest__throwBadRequestException(Game game, Category category, String expected) {
        ProductGroupEditCreationRequest request = new ProductGroupEditCreationRequest();
        request.setName("str");
        request.setDescription("str2");
        request.setCategoryId(1L);
        request.setGameId(1L);
        request.setProductsIds(Arrays.asList(1L, 2L));

        Mockito.doReturn(Optional.ofNullable(game)).when(gameRepository).findById(request.getGameId());

        Mockito.doReturn(Optional.ofNullable(category)).when(categoryRepository).findById(request.getCategoryId());

        List<Product> products = new LinkedList<>();
        Mockito.doReturn(products).when(productRepository).findAllById(request.getProductsIds());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productGroupService.save(request);
        });

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void save__correctRequest__saveToDb() {
        ProductGroupEditCreationRequest request = new ProductGroupEditCreationRequest();
        request.setName("str");
        request.setDescription("str2");
        request.setCategoryId(1L);
        request.setGameId(1L);
        request.setProductsIds(Arrays.asList(1L, 2L));

        Game game = new Game();
        Mockito.doReturn(Optional.of(game)).when(gameRepository).findById(request.getGameId());

        Category category = new Category();
        Mockito.doReturn(Optional.of(category)).when(categoryRepository).findById(request.getCategoryId());

        List<Product> products = new LinkedList<>();
        Mockito.doReturn(products).when(productRepository).findAllById(request.getProductsIds());

        ProductsGroup expected = new ProductsGroup();
        expected.setName(request.getName());
        expected.setDescription(request.getDescription());
        expected.setGame(game);
        expected.setCategory(category);
        expected.setProducts(products);

        productGroupService.save(request);

        ArgumentCaptor<ProductsGroup> productsGroupCaptor = ArgumentCaptor.forClass(ProductsGroup.class);
        Mockito.verify(productGroupRepository, Mockito.times(1)).save(productsGroupCaptor.capture());

        ProductsGroup actual = productsGroupCaptor.getValue();

        assertEquals(expected, actual);
    }

    static Stream<Arguments> saveByIdDataProvider() {
        return Stream.of(
                Arguments.of(null, new Category(), "Invalid game"),
                Arguments.of(new Game(), null, "Invalid category")
        );
    }

    @ParameterizedTest
    @MethodSource("saveByIdDataProvider")
    void saveById__incorrectRequest__throwBadRequestException(Game game, Category category, String expected) {
        ProductGroupEditCreationRequest request = new ProductGroupEditCreationRequest();
        request.setName("str");
        request.setDescription("str2");
        request.setCategoryId(1L);
        request.setGameId(1L);
        request.setProductsIds(Arrays.asList(1L, 2L));

        Long id = 0L;

        Mockito.doReturn(Optional.of(new ProductsGroup())).when(productGroupRepository).findById(id);

        Mockito.doReturn(Optional.ofNullable(game)).when(gameRepository).findById(request.getGameId());

        Mockito.doReturn(Optional.ofNullable(category)).when(categoryRepository).findById(request.getCategoryId());

        List<Product> products = new LinkedList<>();
        Mockito.doReturn(products).when(productRepository).findAllById(request.getProductsIds());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productGroupService.save(id, request);
        });

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void saveById__incorrectRequest__throwBadRequestException() {
        String expected = "Invalid product group";

        Mockito.doReturn(Optional.ofNullable(null)).when(productGroupRepository).findById(0L);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productGroupService.save(0L, new ProductGroupEditCreationRequest());
        });

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void saveById__correctRequest__saveToDb() {
        ProductGroupEditCreationRequest request = new ProductGroupEditCreationRequest();
        request.setName("str");
        request.setDescription("str2");
        request.setCategoryId(1L);
        request.setGameId(1L);
        request.setProductsIds(Arrays.asList(1L, 2L));

        Long id = 0L;

        Mockito.doReturn(Optional.of(new ProductsGroup())).when(productGroupRepository).findById(id);

        Game game = new Game();
        Mockito.doReturn(Optional.of(game)).when(gameRepository).findById(request.getGameId());

        Category category = new Category();
        Mockito.doReturn(Optional.of(category)).when(categoryRepository).findById(request.getCategoryId());

        List<Product> products = new LinkedList<>();
        Mockito.doReturn(products).when(productRepository).findAllById(request.getProductsIds());

        ProductsGroup expected = new ProductsGroup();
        expected.setId(id);
        expected.setName(request.getName());
        expected.setDescription(request.getDescription());
        expected.setGame(game);
        expected.setCategory(category);
        expected.setProducts(products);

        productGroupService.save(id, request);

        ArgumentCaptor<ProductsGroup> productsGroupCaptor = ArgumentCaptor.forClass(ProductsGroup.class);
        Mockito.verify(productGroupRepository, Mockito.times(1)).save(productsGroupCaptor.capture());

        ProductsGroup actual = productsGroupCaptor.getValue();

        assertEquals(expected, actual);
    }

    @Test
    void findRequest__idNotInDb__throwBadRequestException() {
        String expected = "Product group not found";

        Mockito.doReturn(Optional.ofNullable(null)).when(productGroupRepository).findById(0L);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            productGroupService.findRequest(0L);
        });

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void findRequest__idInDb__returnRequest() {
        Category category = new Category();
        category.setId(2L);

        Game game = new Game();
        game.setId(3L);

        Product product1 = new Product();
        product1.setId(4L);
        Product product2 = new Product();
        product2.setId(5L);
        List<Product> products = Arrays.asList(product1, product2);

        ProductsGroup productsGroup = new ProductsGroup();
        productsGroup.setName("str");
        productsGroup.setDescription("str2");
        productsGroup.setCategory(category);
        productsGroup.setGame(game);
        productsGroup.setProducts(products);

        ProductGroupEditCreationRequest expected = new ProductGroupEditCreationRequest();
        expected.setName(productsGroup.getName());
        expected.setDescription(productsGroup.getDescription());
        expected.setCategoryId(productsGroup.getCategory().getId());
        expected.setGameId(productsGroup.getGame().getId());
        expected.setProductsIds(Arrays.asList(product1.getId(), product2.getId()));

        Mockito.doReturn(Optional.of(productsGroup)).when(productGroupRepository).findById(0L);

        ProductGroupEditCreationRequest actual = productGroupService.findRequest(0L);

        assertEquals(expected, actual);
    }

    @Test
    void delete__idCorrect__deleteFromDb() {
        Long excepted = 0L;

        productGroupService.delete(excepted);

        ArgumentCaptor<Long> productsGroupIdCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(productGroupRepository, Mockito.times(1)).deleteById(productsGroupIdCaptor.capture());

        Long actual = productsGroupIdCaptor.getValue();

        assertSame(excepted, actual);
    }
}