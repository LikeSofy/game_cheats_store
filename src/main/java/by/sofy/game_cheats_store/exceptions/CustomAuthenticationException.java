package by.sofy.game_cheats_store.exceptions;

public class CustomAuthenticationException extends RuntimeException{
    public CustomAuthenticationException() {
        super();
    }

    public CustomAuthenticationException(String message) {
        super(message);
    }

    public CustomAuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CustomAuthenticationException(Throwable cause) {
        super(cause);
    }
}
