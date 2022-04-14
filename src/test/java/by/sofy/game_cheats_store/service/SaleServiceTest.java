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

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SaleServiceTest {
    @Autowired
    @Mock
    SaleService saleService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    ProductRepository productRepository;

    @MockBean
    SaleRepository saleRepository;

    @Test
    void findAll() {
        List<Sale> expected = new LinkedList<>();
        Mockito.doReturn(expected).when(saleRepository).findAll();
        List<Sale> actual = saleRepository.findAll();

        assertSame(expected, actual);
    }
    static Stream<Arguments> saveDataProvider() {
        return Stream.of(
                Arguments.of(new SaleCreationRequest(2L, 3L), "Not found user by id: 2"),
                Arguments.of(new SaleCreationRequest(0L, 3L), "Not found product by id: 3"),
                Arguments.of(new SaleCreationRequest(0L, 1L), "User already buy this product")
        );
    }

    @ParameterizedTest
    @MethodSource("saveDataProvider")
    void save__invalidRequest__throwBadRequestException(SaleCreationRequest request, String expected) {
        User user = new User();
        Product product = new Product();

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(0L);
        Mockito.doReturn(Optional.of(product)).when(productRepository).findById(1L);
        Mockito.doReturn(Optional.of(new Sale())).when(saleRepository).findByUserAndProduct(user, product);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            saleService.save(request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void save__validRequest__saveInDb() {
        User user = new User();
        Product product = new Product();

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(0L);
        Mockito.doReturn(Optional.of(product)).when(productRepository).findById(1L);

        SaleCreationRequest request = new SaleCreationRequest(0L, 1L);

        Sale expected = new Sale();
        expected.setProduct(product);
        expected.setUser(user);

        saleService.save(request);

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        Mockito.verify(saleRepository, Mockito.times(1)).save(saleCaptor.capture());

        Sale actual = saleCaptor.getValue();

        assertEquals(expected, actual);
    }

    static Stream<Arguments> buyProductByUserDataProvider() {
        Principal principalUserNoInDb = Mockito.mock(Principal.class);
        Mockito.doReturn("user").when(principalUserNoInDb).getName();
        Principal principalUserHaveProduct = Mockito.mock(Principal.class);
        Mockito.doReturn("user_have_product").when(principalUserHaveProduct).getName();
        Principal principalUserNoHaveProduct = Mockito.mock(Principal.class);
        Mockito.doReturn("user_no_have_product").when(principalUserNoHaveProduct).getName();
        return Stream.of(
                Arguments.of(null, new BuyRequest(1L), new CustomAuthenticationException("Invalid authorize")),
                Arguments.of(principalUserNoInDb, new BuyRequest(1L), new CustomAuthenticationException("Invalid authorize")),
                Arguments.of(principalUserNoHaveProduct, new BuyRequest(3L), new BadRequestException("Not found product by id: 3")),
                Arguments.of(principalUserHaveProduct, new BuyRequest(0L), new BadRequestException("User already buy this product")),
                Arguments.of(principalUserNoHaveProduct, new BuyRequest(0L), new BadRequestException("Insufficient funds"))
        );
    }

    @ParameterizedTest
    @MethodSource("buyProductByUserDataProvider")
    void buyProductByUser__invalidRequest__throwException(Principal principal, BuyRequest request, Exception expected) {
        User userHaveProduct = new User();
        Product product1 = new Product();
        product1.setPrice(BigDecimal.valueOf(20));
        Product product2 = new Product();
        product2.setPrice(BigDecimal.valueOf(5));

        Mockito.doReturn(Optional.of(userHaveProduct)).when(userRepository).findByUsername("user_have_product");
        Mockito.doReturn(Optional.of(product1)).when(productRepository).findById(0L);
        Mockito.doReturn(Optional.of(product2)).when(productRepository).findById(1L);
        Mockito.doReturn(Optional.of(new Sale())).when(saleRepository).findByUserAndProduct(userHaveProduct, product1);

        User userNoHaveProduct = new User();
        userNoHaveProduct.setBalance(BigDecimal.TEN);

        Mockito.doReturn(Optional.of(userNoHaveProduct)).when(userRepository).findByUsername("user_no_have_product");


        Exception actual = assertThrows(Exception.class, () -> {
            saleService.buyProductByUser(principal, request);
        });

        assertEquals(expected.getMessage(), actual.getMessage());
    }

    @Test
    void buyProductByUser__validRequest__saveSaleAndUpdateUser() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.doReturn("user").when(principal).getName();
        User user = new User();
        user.setBalance(BigDecimal.TEN);
        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(5));

        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername("user");
        Mockito.doReturn(Optional.of(product)).when(productRepository).findById(0L);

        BuyRequest request = new BuyRequest(0L);

        saleService.buyProductByUser(principal, request);

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);
        Mockito.verify(saleRepository, Mockito.times(1)).save(saleCaptor.capture());

        Sale actualSale = saleCaptor.getValue();
        Sale expectedSale = new Sale();
        expectedSale.setUser(user);
        expectedSale.setProduct(product);

        assertEquals(expectedSale, actualSale);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());

        User actualUser = userCaptor.getValue();
        User expectedUser = new User();
        expectedUser.setBalance(BigDecimal.valueOf(5));

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void delete(){
        saleService.delete(0L);

        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(saleRepository, Mockito.times(1)).deleteById(idCaptor.capture());

        Long actual = idCaptor.getValue();
        Long expected = 0L;

        assertEquals(expected, actual);
    }

    static Stream<Arguments> findByPrincipalAndProductIdInvalidPrincipalDataProvider() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.doReturn("user").when(principal).getName();
        return Stream.of(
                Arguments.of((Principal)null),
                Arguments.of(principal)
        );
    }

    @ParameterizedTest
    @MethodSource("findByPrincipalAndProductIdInvalidPrincipalDataProvider")
    void findByPrincipalAndProductId__invalidPrincipal__throwCustomAuthenticationException(Principal principal){
        assertThrows(CustomAuthenticationException.class, () -> {
            saleService.findByPrincipalAndProductId(principal, 0L);
        });
    }

    @Test
    void findByPrincipalAndProductId__saleNoInDb__throwNotFoundException(){
        Principal principal = Mockito.mock(Principal.class);
        Mockito.doReturn("user").when(principal).getName();

        User user = new User();

        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername("user");


        assertThrows(NotFoundException.class, () -> {
            saleService.findByPrincipalAndProductId(principal, 0L);
        });
    }

    @Test
    void findByPrincipalAndProductId__validPrincipal_saleInDb__returnSale(){
        Principal principal = Mockito.mock(Principal.class);
        Mockito.doReturn("user").when(principal).getName();

        User user = new User();

        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername("user");

        Sale expected = new Sale();

        Mockito.doReturn(Optional.of(expected)).when(saleRepository).findByUserAndProduct_Id(user, 0L);


        Sale actual = saleService.findByPrincipalAndProductId(principal, 0L);

        assertSame(expected, actual);

    }

}