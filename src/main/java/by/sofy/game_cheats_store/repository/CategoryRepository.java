package by.sofy.game_cheats_store.repository;

import by.sofy.game_cheats_store.entity.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long>{
    List<Category> findAll();
}
