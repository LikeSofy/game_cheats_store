package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ValidationService {
    private final Validator validator;

    public void checkValidation(Object object){
        Set<ConstraintViolation<Object>> violations = validator.validate(object);

        if (violations.isEmpty()){
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<Object> violation : violations){
            sb.append(violation.getMessage());
            sb.append("<p/>");
        }

        String result = sb.substring(0, sb.length() - 4);
        throw new BadRequestException(result);
    }
}
