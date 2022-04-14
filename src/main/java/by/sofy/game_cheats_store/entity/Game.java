package by.sofy.game_cheats_store.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class Game extends BaseEntity{
    @Column(name = "name", unique = true, length = 60)
    private String name;
}
