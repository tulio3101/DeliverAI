package edu.eci.ahia.model.entity.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Estados del pedido, declarados en el orden lógico del flujo. Como {@code Order.state}
 * se persiste como ORDINAL, el índice coincide con el flujo:
 * Ordenado(0) -> Por pagar(1) -> Pagado(2) -> En preparación(3) -> Enviado(4) ->
 * Listo para recoger(5) -> Completado(6).
 *
 * IMPORTANTE: al ser ORDINAL, reordenar o insertar valores en medio cambia el
 * significado de las filas ya guardadas. Si cambias este orden, hay que limpiar o
 * migrar la tabla orders. Con ddl-auto=update, además, la constraint CHECK de la
 * columna no se refresca sola (ver ALTER TABLE ... DROP CONSTRAINT orders_state_check).
 */
@RequiredArgsConstructor
@Getter
@Schema(description = "Order state")
public enum State {
    @Schema(description = "Order placed / in confirmation (initial)")
    IN_CONFIRMATION,
    @Schema(description = "Pending payment")
    PENDING_PAYMENT,
    @Schema(description = "Paid")
    PAID,
    @Schema(description = "Order in preparation")
    PREPARATION,
    @Schema(description = "Shipped (home delivery)")
    SHIPPED,
    @Schema(description = "Ready for in-store pickup")
    READY_FOR_PICKUP,
    @Schema(description = "Order completed")
    COMPLETED,
}
