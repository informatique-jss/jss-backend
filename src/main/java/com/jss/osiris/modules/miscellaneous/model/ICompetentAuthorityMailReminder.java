package com.jss.osiris.modules.miscellaneous.model;

import java.time.LocalDateTime;

public interface ICompetentAuthorityMailReminder {
    LocalDateTime getStatusDate();

    Integer getEmployeeId();

    Integer getCompetentAuthorityId();

    Integer getProvisionId();

    String getMailId();

}
