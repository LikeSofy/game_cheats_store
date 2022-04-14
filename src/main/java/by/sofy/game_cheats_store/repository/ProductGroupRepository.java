package by.sofy.game_cheats_store.repository;

import by.sofy.game_cheats_store.entity.ProductsGroup;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductGroupRepository extends CrudRepository<ProductsGroup, Long> {
    List<ProductsGroup> findAll();

    List<ProductsGroup> findAllByGame_Id(Long gameId);
}
