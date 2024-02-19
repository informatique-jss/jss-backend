package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamation;
import com.jss.osiris.modules.miscellaneous.repository.SalesReclamationRepository;

@Service
public class SalesReclamationServiceImpl implements SalesReclamationService {

        @Autowired
        SalesReclamationRepository salesReclamationRepository;

        @Override
        public List<SalesReclamation> getReclamations() {
                return IterableUtils.toList(salesReclamationRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesReclamation addOrUpdateReclamation(SalesReclamation salesReclamation) {
                return salesReclamationRepository.save(salesReclamation);
        }

        @Override
        public List<SalesReclamation> getReclamationsByTiersId(Integer idTiers) {
                List<SalesReclamation> reclamation = salesReclamationRepository.findByTiersId(idTiers);
                if (reclamation.size() > 0)
                        return reclamation;
                return null;
        }
}
