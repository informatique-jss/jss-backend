package com.jss.osiris.modules.quotation.service.guichetUnique;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.QualiteNonSedentaire;
import com.jss.osiris.modules.quotation.repository.guichetUnique.QualiteNonSedentaireRepository;

@Service
public class QualiteNonSedentaireServiceImpl implements QualiteNonSedentaireService {

    @Autowired
    QualiteNonSedentaireRepository QualiteNonSedentaireRepository;

    @Override
    @Cacheable(value = "qualiteNonSedentaireList", key = "#root.methodName")
    public List<QualiteNonSedentaire> getQualiteNonSedentaire() {
        return IterableUtils.toList(QualiteNonSedentaireRepository.findAll());
    }
}
