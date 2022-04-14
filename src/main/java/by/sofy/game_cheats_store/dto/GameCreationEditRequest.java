package by.sofy.game_cheats_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameCreationEditRequest {
    @NotNull(message = "Name can't be empty")
    @Size(min = 4, max = 30, message = "Name must be longer than {min} characters and shorter than {max}.")
    private String name;
}
