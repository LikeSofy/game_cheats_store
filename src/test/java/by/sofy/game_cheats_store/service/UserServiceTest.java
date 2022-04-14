package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.RefillRequest;
import by.sofy.game_cheats_store.dto.RegistrationRequest;
import by.sofy.game_cheats_store.dto.UserEditRequest;
import by.sofy.game_cheats_store.entity.Product;
import by.sofy.game_cheats_store.entity.User;
import by.sofy.game_cheats_store.entity.UserRoles;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.CustomAuthenticationException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class UserServiceTest {

    @Autowired
    @Mock
    UserService userService;

    @MockBean
    BCryptPasswordEncoder passwordEncoder;

    @MockBean
    UserRepository userRepository;

    @Test
    void findAll() {
        List<User> expected = new LinkedList<>();
        Mockito.doReturn(expected).when(userRepository).findAll();
        List<User> actual = userService.findAllUsers();

        assertSame(expected, actual);
    }

    @Test
    void loadUserByUsername_userInDatabase_returnUser() {
        User expected = new User();
        expected.setUsername("username");
        Mockito.doReturn(Optional.of(expected)).when(userRepository).findByUsername(expected.getUsername());
        UserDetails actual = userService.loadUserByUsername(expected.getUsername());

        assertEquals(expected, actual);
    }

    @Test
    void loadUserByUsername_userNotInDatabase_throwUsernameNotFoundException() {
        String expected = "User not found";

        Mockito.doReturn(Optional.empty()).when(userRepository).findByUsername(ArgumentMatchers.anyString());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("username");
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    static Stream<Arguments> findBalanceForCurrentUserDataProvider() {
        return Stream.of(
                Arguments.of(Mockito.mock(Principal.class), BigDecimal.valueOf(0)),
                Arguments.of(null, BigDecimal.valueOf(0))
        );
    }

    @ParameterizedTest
    @MethodSource("findBalanceForCurrentUserDataProvider")
    void findBalanceForCurrentUser__userNotFound__return(Principal principal, BigDecimal expected) {
        Mockito.doReturn(Optional.empty()).when(userRepository).findByUsername(ArgumentMatchers.notNull());
        BigDecimal actual = userService.findBalanceForCurrentUser(principal);

        assertEquals(expected, actual);
    }

    @Test
    void findBalanceForCurrentUser__userFound__return() {
        User user = new User();
        BigDecimal expected = BigDecimal.valueOf(5);
        user.setBalance(expected);
        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername(ArgumentMatchers.notNull());
        Principal principal = Mockito.mock(Principal.class);
        Mockito.doReturn("login").when(principal).getName();
        BigDecimal actual = userService.findBalanceForCurrentUser(principal);

        assertEquals(expected, actual);
    }

    @Test
    void findRequestById__idNotInDb__throwNotFoundException(){
        String expected = "User not found";

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.findRequestById(0L);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void findRequestById__idInDb__throwNotFoundException(){
        User user = new User();
        user.setUsername("username");
        user.setRole(UserRoles.ROLE_USER);

        UserEditRequest expected = new UserEditRequest();
        expected.setLogin(user.getUsername());
        expected.setRoles(user.getRole().name());

        Mockito.doReturn(Optional.of(user)).when(userRepository).findById(0L);
        UserEditRequest actual = userService.findRequestById(0L);

        assertEquals(expected, actual);
    }

    static Stream<Arguments> registerByRegistrationRequestUserDataProvider() {
        return Stream.of(
                Arguments.of(new RegistrationRequest("login", "", ""), "Password required"),
                Arguments.of(new RegistrationRequest("login", "password", "otherPassword"), "Password mismatch"),
                Arguments.of(new RegistrationRequest("login.", "Passw0rds", "Passw0rds"), "Login must consist of Latin letters and symbols \"-\" and \"_\"."),
                Arguments.of(new RegistrationRequest("log", "Passw0rds", "Passw0rds"), "Login must be longer than 4 characters and shorter than 16."),
                Arguments.of(new RegistrationRequest("loginloginloginlogin", "Passw0rds", "Passw0rds"), "Login must be longer than 4 characters and shorter than 16."),
                Arguments.of(new RegistrationRequest("login", "password", "password"), "The password must be entered a letter and numbers and be longer than 8 characters and shorter than 16."),
                Arguments.of(new RegistrationRequest("reg_user", "Passw0rds", "Passw0rds"), "User with this username registered")
        );
    }

    @ParameterizedTest
    @MethodSource("registerByRegistrationRequestUserDataProvider")
    void registerByRegistrationRequest__invalidRequest__trowBadRequestException(RegistrationRequest request, String expected) {
        Mockito.doReturn(Optional.of(new User())).when(userRepository).findByUsername("reg_user");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.register(request);
        });

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void registerByRegistrationRequest__validRequest__saveInDb() {
        RegistrationRequest request = new RegistrationRequest("login", "Passw0rds", "Passw0rds");

        Mockito.doReturn("encodedPassword").when(passwordEncoder).encode("Passw0rds");

        User expected = new User();
        expected.setUsername(request.getLogin());
        expected.setPassword("encodedPassword");

        userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());

        User actual = userCaptor.getValue();

        assertEquals(expected, actual);
    }

    static Stream<Arguments> registerUserEditRequestUserDataProvider() {
        return Stream.of(
                Arguments.of(new UserEditRequest("login", "ROLE_USER", "", ""), "Password required"),
                Arguments.of(new UserEditRequest("login", "ROLE_USER", "password", "otherPassword"), "Password mismatch"),
                Arguments.of(new UserEditRequest("login.", "ROLE_USER", "Passw0rds", "Passw0rds"), "Login must consist of Latin letters and symbols \"-\" and \"_\"."),
                Arguments.of(new UserEditRequest("log", "ROLE_USER", "Passw0rds", "Passw0rds"), "Login must be longer than 4 characters and shorter than 16."),
                Arguments.of(new UserEditRequest("loginloginloginlogin", "ROLE_USER", "Passw0rds", "Passw0rds"), "Login must be longer than 4 characters and shorter than 16."),
                Arguments.of(new UserEditRequest("login", "", "Passw0rds", "Passw0rds"), "Role can't be empty"),
                Arguments.of(new UserEditRequest("login", "ROLE_USER", "password", "password"), "The password must be entered a letter and numbers and be longer than 8 characters and shorter than 16."),
                Arguments.of(new UserEditRequest("reg_user", "ROLE_USER", "Passw0rds", "Passw0rds"), "User with this username registered")
        );
    }

    @ParameterizedTest
    @MethodSource("registerUserEditRequestUserDataProvider")
    void registerUserEditRequest__invalidRequest__trowBadRequestException(UserEditRequest request, String expected) {
        Mockito.doReturn(Optional.of(new User())).when(userRepository).findByUsername("reg_user");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.register(request);
        });

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void registerUserEditRequest__validRequest__saveInDb() {
        UserEditRequest request = new UserEditRequest("login", "ROLE_USER", "Passw0rds", "Passw0rds");

        Mockito.doReturn("encodedPassword").when(passwordEncoder).encode("Passw0rds");

        User expected = new User();
        expected.setUsername(request.getLogin());
        expected.setPassword("encodedPassword");
        expected.setRole(UserRoles.ROLE_USER);

        userService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());

        User actual = userCaptor.getValue();

        assertEquals(expected, actual);
    }


    @Test
    void edit__idInDb_noPasswordInRequest__saveUserInDb() {
        User excepted = new User();
        excepted.setUsername("login");
        excepted.setRole(UserRoles.ROLE_USER);

        UserEditRequest request = new UserEditRequest();
        request.setLogin("login");
        request.setRoles(UserRoles.ROLE_USER.name());

        Mockito.doReturn(Optional.of(new User())).when(userRepository).findById(1L);

        userService.edit(1L, request);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());

        User actual = userCaptor.getValue();

        assertEquals(excepted, actual);
    }

    @Test
    void edit__idInDb_withPasswordInRequest__saveUserInDb() {
        User excepted = new User();
        excepted.setUsername("login");
        excepted.setRole(UserRoles.ROLE_USER);
        excepted.setPassword("encodedPassword");

        UserEditRequest request = new UserEditRequest();
        request.setLogin("login");
        request.setRoles(UserRoles.ROLE_USER.name());
        request.setPassword("newPassword1");
        request.setReplyPassword("newPassword1");

        Mockito.doReturn(Optional.of(new User())).when(userRepository).findById(1L);
        Mockito.doReturn("encodedPassword").when(passwordEncoder).encode("newPassword1");

        userService.edit(1L, request);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());

        User actual = userCaptor.getValue();

        assertEquals(excepted, actual);
    }

    @Test
    void edit__passwordMismatch__throwBadRequestException() {
        String expected = "Password mismatch";

        UserEditRequest request = new UserEditRequest();
        request.setLogin("login");
        request.setRoles(UserRoles.ROLE_USER.name());
        request.setPassword("newPassword1");
        request.setReplyPassword("newPassword2");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.edit(1L, request);
        });

        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void edit__idNotInDb_noPasswordInRequest__throwNotFoundException() {
        String expected = "Not fount user by id";

        UserEditRequest request = new UserEditRequest();
        request.setLogin("login");
        request.setRoles(UserRoles.ROLE_USER.name());

        Mockito.doReturn(Optional.empty()).when(userRepository).findById(1L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.edit(1L, request);
        });

        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    void edit__idNotInDb_passwordNotMismatch__throwNotFoundException() {
        String expected = "Not fount user by id";

        UserEditRequest request = new UserEditRequest();
        request.setLogin("login");
        request.setRoles(UserRoles.ROLE_USER.name());
        request.setPassword("Password1");
        request.setReplyPassword("Password1");

        Mockito.doReturn(Optional.empty()).when(userRepository).findById(1L);

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            userService.edit(1L, request);
        });

        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    void checkUniqueUsername__username1EqualsUsername2__noThrow() {
        assertDoesNotThrow(() -> {
            userService.checkUniqueUsername("username", "username");
        });
    }

    @Test
    void checkUniqueUsername__username1NotEqualsUsername2_UsernameNotInDb__noThrow() {
        Mockito.doReturn(Optional.empty()).when(userRepository).findByUsername(ArgumentMatchers.anyString());

        assertDoesNotThrow(() -> {
            userService.checkUniqueUsername("username", "username");
        });
    }

    @Test
    void checkUniqueUsername__username1NotEqualsUsername2_UsernameInDb__throwBadRequestException() {
        String expected = "User with this username registered";

        Mockito.doReturn(Optional.of(new User())).when(userRepository).findByUsername(ArgumentMatchers.anyString());

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            userService.checkUniqueUsername("username1", "username2");
        });

        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    void refill__principalNotInDb__throwCustomAuthenticationException() {
        String expected = "User not found";

        Principal principal = Mockito.mock(Principal.class);
        RefillRequest request = new RefillRequest();

        CustomAuthenticationException exception = assertThrows(CustomAuthenticationException.class, () -> {
            userService.refill(principal, request);
        });

        String actual = exception.getMessage();
        assertEquals(expected, actual);
    }

    @Test
    void refill__principalInDb__save() {
        Principal principal = Mockito.mock(Principal.class);
        Mockito.doReturn("name").when(principal).getName();
        RefillRequest request = new RefillRequest();
        request.setAmount(BigDecimal.TEN);

        User user = new User();
        user.setBalance(BigDecimal.valueOf(15));

        Mockito.doReturn(Optional.of(user)).when(userRepository).findByUsername("name");

        userService.refill(principal, request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(userRepository, Mockito.times(1)).save(userCaptor.capture());


        User actual = userCaptor.getValue();

        User expected = new User();
        expected.setBalance(BigDecimal.valueOf(25));

        assertEquals(expected, actual);
    }
}