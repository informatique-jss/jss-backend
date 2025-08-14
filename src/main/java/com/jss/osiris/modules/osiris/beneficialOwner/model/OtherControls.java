package com.jss.osiris.modules.osiris.beneficialOwner.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class OtherControls {

    private boolean controlsGeneralMeetings;
    private boolean hasPowerToAppointOrRemoveManagement;
    private boolean isLegalRepresentative;

    public boolean isControlsGeneralMeetings() {
        return controlsGeneralMeetings;
    }

    public void setControlsGeneralMeetings(boolean controlsGeneralMeetings) {
        this.controlsGeneralMeetings = controlsGeneralMeetings;
    }

    public boolean isHasPowerToAppointOrRemoveManagement() {
        return hasPowerToAppointOrRemoveManagement;
    }

    public void setHasPowerToAppointOrRemoveManagement(boolean hasPowerToAppointOrRemoveManagement) {
        this.hasPowerToAppointOrRemoveManagement = hasPowerToAppointOrRemoveManagement;
    }

    public boolean isLegalRepresentative() {
        return isLegalRepresentative;
    }

    public void setLegalRepresentative(boolean isLegalRepresentative) {
        this.isLegalRepresentative = isLegalRepresentative;
    }

}
