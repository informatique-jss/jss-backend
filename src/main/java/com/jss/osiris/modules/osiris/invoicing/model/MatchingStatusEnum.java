package com.jss.osiris.modules.osiris.invoicing.model;

public enum MatchingStatusEnum {
    MISSING_IN_OSIRIS("Manquant dans OSIRIS"),
    MISSING_IN_INPI_EXTRACT("Manquant dans l'extrait INPI"),
    AMOUNT_MISMATCH("Montants différents");

    public final String label;

    private MatchingStatusEnum(String label) {
        this.label = label;
    }
}
