package by.sofy.game_cheats_store.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Data
public class Product extends BaseEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "path")
    private String path;

    @Column(name = "price")
    private BigDecimal price;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_group_id")
    private ProductsGroup productsGroup;
}
