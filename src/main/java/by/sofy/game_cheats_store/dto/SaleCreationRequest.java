package by.sofy.game_cheats_store.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaleCreationRequest {
    private Long userId;
    private Long productId;
}
