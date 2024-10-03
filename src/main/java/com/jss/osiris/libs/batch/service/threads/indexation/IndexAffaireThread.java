package com.jss.osiris.libs.batch.service.threads.indexation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.batch.model.Batch;
import com.jss.osiris.modules.osiris.miscellaneous.model.IId;
import com.jss.osiris.modules.osiris.quotation.service.AffaireService;

@Service
public class IndexAffaireThread extends IndexThread {

    @Autowired
    AffaireService affaireService;

    @Override
    public String getBatchCode() {
        return Batch.REINDEX_AFFAIRE;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IId getEntity(Integer entityId) {
        return affaireService.getAffaire(entityId);
    }

}
