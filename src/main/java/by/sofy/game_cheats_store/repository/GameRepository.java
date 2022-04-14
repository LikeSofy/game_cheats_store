package by.sofy.game_cheats_store.repository;

import by.sofy.game_cheats_store.entity.Game;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GameRepository extends CrudRepository<Game, Long> {
    List<Game> findAll();
}
