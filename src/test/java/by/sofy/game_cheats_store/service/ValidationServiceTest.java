package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.exceptions.BadRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ValidationServiceTest {
    @Autowired
    @Mock
    ValidationService validationService;

    @MockBean
    Validator validator;

    static Stream<Arguments> checkValidationDataProvider() {
        ConstraintViolation<Object> constraintViolation1 = Mockito.mock(ConstraintViolation.class);
        Mockito.doReturn("First error message").when(constraintViolation1).getMessage();

        ConstraintViolation<Object> constraintViolation2 = Mockito.mock(ConstraintViolation.class);
        Mockito.doReturn("Second error message").when(constraintViolation2).getMessage();

        MultipartFile multipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(false).when(multipartFile).isEmpty();
        MultipartFile emptyMultipartFile = Mockito.mock(MultipartFile.class);
        Mockito.doReturn(true).when(emptyMultipartFile).isEmpty();
        return Stream.of(
                Arguments.of(new LinkedHashSet(List.of(constraintViolation1, constraintViolation2))
                        , "First error message<p/>Second error message"),
                Arguments.of(Set.of(constraintViolation1)
                        , "First error message")
        );
    }

    @ParameterizedTest
    @MethodSource("checkValidationDataProvider")
    void checkValidation__invalidObject__throwBadRequestException(Set<ConstraintViolation<Object>> violations, String expected) {
        Object object = new Object();

        Mockito.doReturn(violations).when(validator).validate(object);
        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            validationService.checkValidation(object);
        });
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    @Test
    void checkValidation__objectValid__noThrowBadRequestException() {
        Object object = new Object();
        Mockito.doReturn(new HashSet<ConstraintViolation<Object>>()).when(validator).validate(object);
        assertDoesNotThrow(() -> validationService.checkValidation(object));
    }

}