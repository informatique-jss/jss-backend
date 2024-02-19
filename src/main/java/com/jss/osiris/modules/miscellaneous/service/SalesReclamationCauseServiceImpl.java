package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamationCause;
import com.jss.osiris.modules.miscellaneous.repository.SalesReclamationCauseRepository;

@Service
public class SalesReclamationCauseServiceImpl implements SalesReclamationCauseService {

        @Autowired
        SalesReclamationCauseRepository salesReclamationCauseRepository;

        @Override
        public List<SalesReclamationCause> getReclamationCauses() {
                return IterableUtils.toList(salesReclamationCauseRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesReclamationCause addOrUpdateReclamationCause(SalesReclamationCause salesReclamationCause) {
                return salesReclamationCauseRepository.save(salesReclamationCause);
        }
}
