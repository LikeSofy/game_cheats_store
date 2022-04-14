package by.sofy.game_cheats_store.repository;

import by.sofy.game_cheats_store.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();

    Optional<User> findByUsername(String username);
}
