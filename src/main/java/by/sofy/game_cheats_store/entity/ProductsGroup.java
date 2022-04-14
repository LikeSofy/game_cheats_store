package by.sofy.game_cheats_store.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class ProductsGroup extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_group_id")
    private List<Product> products;
}
