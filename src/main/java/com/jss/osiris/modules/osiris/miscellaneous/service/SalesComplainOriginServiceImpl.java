package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.miscellaneous.model.SalesComplainOrigin;
import com.jss.osiris.modules.osiris.miscellaneous.repository.SalesComplainOriginRepository;

@Service
public class SalesComplainOriginServiceImpl implements SalesComplainOriginService {

        @Autowired
        SalesComplainOriginRepository salesComplainOriginRepository;

        @Override
        public List<SalesComplainOrigin> getComplainOrigins() {
                return IterableUtils.toList(salesComplainOriginRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesComplainOrigin addOrUpdateComplainOrigin(SalesComplainOrigin salesComplainOrigin) {
                return salesComplainOriginRepository.save(salesComplainOrigin);
        }
}
