package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.time.LocalDateTime;
import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.libs.exception.OsirisValidationException;
import com.jss.osiris.modules.quotation.model.guichetUnique.FormaliteGuichetUnique;
import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormaliteStatusHistoryItem;

public interface GuichetUniqueDelegateService {
        public List<FormaliteGuichetUnique> getAllFormalitiesByDate(LocalDateTime createdAfter,
                        LocalDateTime updatedAfter)
                        throws OsirisException, OsirisClientMessageException;

        public List<FormaliteGuichetUnique> getAllFormalitiesByRefenceMandataire(String reference)
                        throws OsirisException, OsirisClientMessageException;

        public FormaliteGuichetUnique getFormalityById(Integer id)
                        throws OsirisException, OsirisClientMessageException;

        public List<FormaliteStatusHistoryItem> getFormalityStatusHistoriesById(Integer id)
                        throws OsirisException, OsirisClientMessageException;

        public FormaliteGuichetUnique getAnnualAccountById(Integer id)
                        throws OsirisException, OsirisClientMessageException;

        public List<FormaliteStatusHistoryItem> getAnnualAccountStatusHistoriesById(Integer id)
                        throws OsirisException, OsirisClientMessageException;

        public void refreshFormalitiesFromLastHour()
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException;

        public void refreshAllOpenFormalities()
                        throws OsirisValidationException, OsirisException, OsirisClientMessageException;
}
