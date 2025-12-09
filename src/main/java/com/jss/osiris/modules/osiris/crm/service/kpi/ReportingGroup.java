package com.jss.osiris.modules.osiris.crm.service.kpi;

public enum ReportingGroup {
    FORMALITIES("Formalités"),
    ANNOUNCEMENTS("Annonces"),
    DEPOSIT("Dépôt"),
    OTHER("Autre"),
    DOMICILIATION("Domiciliation"),
    JSS_FR("JSS.fr"),
    GESTION("Gestion"),
    FOURNITURES("Fournitures");

    private String label;

    ReportingGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}