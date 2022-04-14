package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.GameCreationEditRequest;
import by.sofy.game_cheats_store.entity.Category;
import by.sofy.game_cheats_store.entity.Game;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.GameRepository;
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

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GameServiceTest {
    @Autowired
    @Mock
    GameService gameService;

    @MockBean
    GameRepository gameRepository;

    @Test
    void findAll(){
        List<Game> expected = new LinkedList<>();

        Mockito.doReturn(expected).when(gameRepository).findAll();

        List<Game> actual = gameService.findAll();

        assertSame(expected, actual);
    }

    @Test
    void findRequestById__idNotInDb__throwNotFoundException(){
        String expected = "Not found game by id: 0";

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            gameService.findRequestById(0L);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void findRequestById__idInDb__returnRequest(){
        Game game = new Game();
        game.setName("name");

        Mockito.doReturn(Optional.of(game)).when(gameRepository).findById(0L);

        GameCreationEditRequest expected = new GameCreationEditRequest();
        expected.setName(game.getName());

        GameCreationEditRequest actual = gameService.findRequestById(0L);

        assertEquals(expected, actual);
    }

    static Stream<Arguments> createDataProvider() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(false).when(multipartFile).isEmpty();
        MultipartFile emptyMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(true).when(emptyMultipartFile).isEmpty();
        return Stream.of(
                Arguments.of(new GameCreationEditRequest("")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new GameCreationEditRequest("invalidNameLengthMoreThan30********************************************")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new GameCreationEditRequest(null)
                        , "Name can't be empty")
        );
    }

    @ParameterizedTest
    @MethodSource("createDataProvider")
    void create__invalidRequests__throwException(GameCreationEditRequest request, String expected) {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.create(request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void create__validRequest__saveInDb() {
        GameCreationEditRequest request = new GameCreationEditRequest();
        request.setName("name");

        Game excepted = new Game();
        excepted.setName(request.getName());

        gameService.create(request);

        ArgumentCaptor<Game> categoryCaptor = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository, Mockito.times(1)).save(categoryCaptor.capture());

        Game actual = categoryCaptor.getValue();

        assertEquals(excepted, actual);
    }

    static Stream<Arguments> editDataProvider() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(false).when(multipartFile).isEmpty();
        MultipartFile emptyMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(true).when(emptyMultipartFile).isEmpty();
        return Stream.of(
                Arguments.of(new GameCreationEditRequest("")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new GameCreationEditRequest("invalidNameLengthMoreThan30********************************************")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new GameCreationEditRequest(null)
                        , "Name can't be empty")
        );
    }

    @ParameterizedTest
    @MethodSource("createDataProvider")
    void edit__invalidRequests__throwException(GameCreationEditRequest request, String expected) {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.edit(0L, request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void edit__validRequests_idNotInDb__throwException() {
        String expected = "Not found game by id: 0";

        GameCreationEditRequest request = new GameCreationEditRequest();
        request.setName("name");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            gameService.edit(0L, request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void edit__validRequest_idInDb__saveInDb() {
        GameCreationEditRequest request = new GameCreationEditRequest();
        request.setName("name");

        Game excepted = new Game();
        excepted.setName(request.getName());
        excepted.setId(0L);

        Mockito.doReturn(Optional.of(new Category())).when(gameRepository).findById(0L);

        gameService.edit(0L, request);

        ArgumentCaptor<Game> categoryCaptor = ArgumentCaptor.forClass(Game.class);
        Mockito.verify(gameRepository, Mockito.times(1)).save(categoryCaptor.capture());

        Game actual = categoryCaptor.getValue();

        assertEquals(excepted, actual);
    }

    @Test
    void deleteById() {
        Long excepted = 0L;

        gameService.deleteById(excepted);

        ArgumentCaptor<Long> categoryIdCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(gameRepository, Mockito.times(1)).deleteById(categoryIdCaptor.capture());

        Long actual = categoryIdCaptor.getValue();

        assertSame(excepted, actual);
    }
}