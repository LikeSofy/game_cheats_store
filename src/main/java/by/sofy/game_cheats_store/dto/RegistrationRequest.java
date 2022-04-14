package by.sofy.game_cheats_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationRequest {
    @Pattern(regexp = "[a-zA-Z0-9_\\-]*", message = "Login must consist of Latin letters and symbols \"-\" and \"_\".")
    @Size(min = 4, max = 16, message = "Login must be longer than 4 characters and shorter than 16.")
    private String login;
    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9].{8,16}", message = "The password must be entered a letter and numbers and be longer than 8 characters and shorter than 16.")
    private String password;
    private String replyPassword;
}
