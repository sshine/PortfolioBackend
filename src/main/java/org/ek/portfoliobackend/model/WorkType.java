package org.ek.portfoliobackend.model;

import lombok.Getter;

@Getter
public enum WorkType {

    PAVING_CLEANING("Fliserens"),
    WOODEN_DECK_CLEANING ("Rens af træterrasse"),
    ROOF_CLEANING ("Tagrens"),
    FACADE_CLEANING ("Facaderens");

    private final String displayName;

    WorkType(String displayName) {
        this.displayName = displayName;
    }

}
