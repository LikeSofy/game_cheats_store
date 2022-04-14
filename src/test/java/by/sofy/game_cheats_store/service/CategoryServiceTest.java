package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.CategoryCreationEditRequest;
import by.sofy.game_cheats_store.entity.Category;
import by.sofy.game_cheats_store.entity.Game;
import by.sofy.game_cheats_store.entity.Product;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.CategoryRepository;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CategoryServiceTest {
    @Autowired
    @Mock
    CategoryService categoryService;

    @MockBean
    CategoryRepository categoryRepository;

    @Test
    void findAll(){
        List<Category> expected = new LinkedList<>();

        Mockito.doReturn(expected).when(categoryRepository).findAll();

        List<Category> actual = categoryRepository.findAll();

        assertSame(expected, actual);
    }

    @Test
    void findRequestById__idNotInDb__throwNotFoundException(){
        String expected = "Not found category by id: 0";

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            categoryService.findRequestById(0L);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void findRequestById__idInDb__returnRequest(){
        Category category = new Category();
        category.setName("name");

        Mockito.doReturn(Optional.of(category)).when(categoryRepository).findById(0L);

        CategoryCreationEditRequest expected = new CategoryCreationEditRequest();
        expected.setName(category.getName());

        CategoryCreationEditRequest actual = categoryService.findRequestById(0L);

        assertEquals(expected, actual);
    }

    static Stream<Arguments> createDataProvider() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(false).when(multipartFile).isEmpty();
        MultipartFile emptyMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(true).when(emptyMultipartFile).isEmpty();
        return Stream.of(
                Arguments.of(new CategoryCreationEditRequest("")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new CategoryCreationEditRequest("invalidNameLengthMoreThan30********************************************")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new CategoryCreationEditRequest(null)
                        , "Name can't be empty")
        );
    }

    @ParameterizedTest
    @MethodSource("createDataProvider")
    void create__invalidRequests__throwException(CategoryCreationEditRequest request, String expected) {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            categoryService.create(request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void create__validRequest__saveInDb() {
        CategoryCreationEditRequest request = new CategoryCreationEditRequest("name");

        Category excepted = new Category();
        excepted.setName(request.getName());

        categoryService.create(request);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(categoryCaptor.capture());

        Category actual = categoryCaptor.getValue();

        assertEquals(excepted, actual);
    }

    static Stream<Arguments> editDataProvider() {
        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(false).when(multipartFile).isEmpty();
        MultipartFile emptyMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(true).when(emptyMultipartFile).isEmpty();
        return Stream.of(
                Arguments.of(new CategoryCreationEditRequest("")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new CategoryCreationEditRequest("invalidNameLengthMoreThan30********************************************")
                        , "Name must be longer than 4 characters and shorter than 30."),
                Arguments.of(new CategoryCreationEditRequest(null)
                        , "Name can't be empty")
        );
    }

    @ParameterizedTest
    @MethodSource("createDataProvider")
    void edit__invalidRequests__throwException(CategoryCreationEditRequest request, String expected) {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            categoryService.edit(0L, request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void edit__validRequests_idNotInDb__throwException() {
        String expected = "Not found category by id: 0";

        CategoryCreationEditRequest request = new CategoryCreationEditRequest("name");

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            categoryService.edit(0L, request);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void edit__validRequest_idInDb__saveInDb() {
        CategoryCreationEditRequest request = new CategoryCreationEditRequest("name");

        Category excepted = new Category();
        excepted.setName(request.getName());
        excepted.setId(0L);

        Mockito.doReturn(Optional.of(new Category())).when(categoryRepository).findById(0L);

        categoryService.edit(0L, request);

        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        Mockito.verify(categoryRepository, Mockito.times(1)).save(categoryCaptor.capture());

        Category actual = categoryCaptor.getValue();

        assertEquals(excepted, actual);
    }

    @Test
    void deleteById() {
        Long excepted = 0L;

        categoryService.deleteById(excepted);

        ArgumentCaptor<Long> categoryIdCaptor = ArgumentCaptor.forClass(Long.class);
        Mockito.verify(categoryRepository, Mockito.times(1)).deleteById(categoryIdCaptor.capture());

        Long actual = categoryIdCaptor.getValue();

        assertSame(excepted, actual);
    }
}