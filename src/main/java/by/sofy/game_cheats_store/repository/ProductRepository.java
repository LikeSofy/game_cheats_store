package by.sofy.game_cheats_store.repository;

import by.sofy.game_cheats_store.entity.Product;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProductRepository extends CrudRepository<Product, Long> {
    List<Product> findAll();

    List<Product> findAllById(Iterable<Long> ids);

    List<Product> findAllByProductsGroupIsNull();
}
