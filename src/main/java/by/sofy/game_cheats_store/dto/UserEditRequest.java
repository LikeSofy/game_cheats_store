package by.sofy.game_cheats_store.dto;

import com.sun.istack.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditRequest {
    @Pattern(regexp = "[a-zA-Z0-9_\\-]*", message = "Login must consist of Latin letters and symbols \"-\" and \"_\".")
    @Length(min = 4, max = 16, message = "Login must be longer than 4 characters and shorter than 16.")
    private String login;
    @NotEmpty(message = "Role can't be empty")
    private String roles;
    @Nullable
    @Pattern(regexp = "(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z0-9].{8,16}", message = "The password must be entered a letter and numbers and be longer than 8 characters and shorter than 16.")
    private String password;
    private String replyPassword;
}
