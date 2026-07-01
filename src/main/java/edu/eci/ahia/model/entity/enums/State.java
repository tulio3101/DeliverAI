package edu.eci.ahia.model.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@Schema(description = "Order state")
public enum State {
    @Schema(description = "Order in confirmation")
    IN_CONFIRMATION,
    @Schema(description = "Order in preparation")
    PREPARATION,
    @Schema(description = "Order completed")
    COMPLETED,
}
