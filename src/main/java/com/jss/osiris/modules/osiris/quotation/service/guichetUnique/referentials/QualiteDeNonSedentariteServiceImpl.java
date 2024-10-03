package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.QualiteDeNonSedentarite;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.QualiteDeNonSedentariteRepository;

@Service
public class QualiteDeNonSedentariteServiceImpl implements QualiteDeNonSedentariteService {

    @Autowired
    QualiteDeNonSedentariteRepository QualiteDeNonSedentariteRepository;

    @Override
    public List<QualiteDeNonSedentarite> getQualiteDeNonSedentarite() {
        return IterableUtils.toList(QualiteDeNonSedentariteRepository.findAll());
    }
}
