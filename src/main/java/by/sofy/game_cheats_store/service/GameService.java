package by.sofy.game_cheats_store.service;

import by.sofy.game_cheats_store.dto.GameCreationEditRequest;
import by.sofy.game_cheats_store.entity.Game;
import by.sofy.game_cheats_store.exceptions.BadRequestException;
import by.sofy.game_cheats_store.exceptions.NotFoundException;
import by.sofy.game_cheats_store.repository.GameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {
    private final GameRepository gameRepository;
    private final ValidationService validationService;

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public GameCreationEditRequest findRequestById(Long id) {
        Game game = gameRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Not found game by id: %s", id)));
        GameCreationEditRequest request = new GameCreationEditRequest();
        request.setName(game.getName());

        return request;
    }

    public void create(GameCreationEditRequest request) {
        validationService.checkValidation(request);
        Game game = new Game();
        game.setName(request.getName());
        gameRepository.save(game);
    }

    public void edit(Long id, GameCreationEditRequest request) {
        validationService.checkValidation(request);
        gameRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(String.format("Not found game by id: %s", id)));
        Game game = new Game();
        game.setName(request.getName());
        game.setId(id);
        gameRepository.save(game);
    }

    public void deleteById(Long id) {
        gameRepository.deleteById(id);
    }
}
