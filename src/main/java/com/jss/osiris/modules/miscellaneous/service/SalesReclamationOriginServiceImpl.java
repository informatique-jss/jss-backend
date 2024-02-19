package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.SalesReclamationOrigin;
import com.jss.osiris.modules.miscellaneous.repository.SalesReclamationOriginRepository;
import com.jss.osiris.modules.miscellaneous.repository.SalesReclamationProblemRepository;

@Service
public class SalesReclamationOriginServiceImpl implements SalesReclamationOriginService {

        @Autowired
        SalesReclamationOriginRepository salesReclamationOriginRepository;

        @Autowired
        SalesReclamationProblemRepository salesReclamationOrigin;

        @Override
        public List<SalesReclamationOrigin> getReclamationOrigins() {
                return IterableUtils.toList(salesReclamationOriginRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesReclamationOrigin addOrUpdateReclamationOrigin(SalesReclamationOrigin salesReclamationOrigin) {
                return salesReclamationOriginRepository.save(salesReclamationOrigin);
        }
}
