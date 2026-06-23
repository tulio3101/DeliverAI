package edu.eci.ahia.model.entity.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum State {
    IN_CONFIRMATION,
    PREPARATION,
    COMPLETED,
}
