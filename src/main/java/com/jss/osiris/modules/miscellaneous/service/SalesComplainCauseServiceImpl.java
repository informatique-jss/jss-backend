package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.SalesComplainCause;
import com.jss.osiris.modules.miscellaneous.repository.SalesComplainCauseRepository;

@Service
public class SalesComplainCauseServiceImpl implements SalesComplainCauseService {

        @Autowired
        SalesComplainCauseRepository salesComplainCauseRepository;

        @Override
        public List<SalesComplainCause> getComplainCauses() {
                return IterableUtils.toList(salesComplainCauseRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesComplainCause addOrUpdateComplainCause(SalesComplainCause salesComplainCause) {
                return salesComplainCauseRepository.save(salesComplainCause);
        }
}
