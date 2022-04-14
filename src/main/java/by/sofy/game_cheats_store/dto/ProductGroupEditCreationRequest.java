package by.sofy.game_cheats_store.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ProductGroupEditCreationRequest{
    @Size(min = 4, max = 30, message = "Name must be longer than {min} characters and shorter than {max}.")
    private String name;
    @Size(max = 1000, message = "Description must be shorter than {max}.")
    private String description;
    private Long gameId;
    private Long categoryId;
    private List<Long> productsIds;
}
