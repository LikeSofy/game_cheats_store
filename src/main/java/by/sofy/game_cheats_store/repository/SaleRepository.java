package by.sofy.game_cheats_store.repository;

import by.sofy.game_cheats_store.entity.Product;
import by.sofy.game_cheats_store.entity.Sale;
import by.sofy.game_cheats_store.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface SaleRepository extends CrudRepository<Sale, Long> {
    Optional<Sale> findByUserAndProduct(User user, Product product);

    Optional<Sale> findByUserAndProduct_Id(User user, Long productId);

    List<Sale> findAll();
}
