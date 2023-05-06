package com.jss.osiris.modules.quotation.service;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.profile.model.Employee;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;

public interface GuichetUniqueDelegateService {
        public List<FormaliteGuichetUnique> getFormalitiesByDate(LocalDateTime createdAfter, LocalDateTime updatedAfter,
                        Employee employee)
                        throws OsirisException, OsirisClientMessageException;

        public List<FormaliteGuichetUnique> getFormalitiesByRefenceMandataire(String reference, Employee employee)
                        throws OsirisException, OsirisClientMessageException;

        public FormaliteGuichetUnique getFormalityById(Integer id, Employee employee)
                        throws OsirisException, OsirisClientMessageException;

        public void refreshFormalitiesFromLastHour()
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException;

        public void refreshAllOpenFormalities()
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException;
}
