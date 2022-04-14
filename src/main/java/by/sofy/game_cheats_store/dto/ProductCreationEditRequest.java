package by.sofy.game_cheats_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreationEditRequest {
    @Size(min = 4, max = 30, message = "Name must be longer than {min} characters and shorter than {max}.")
    private String name;
    @Min(value = 0, message = "Price can't be less than {value}.")
    private BigDecimal price;
    private MultipartFile file;
}
