package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.miscellaneous.model.SalesComplain;
import com.jss.osiris.modules.miscellaneous.repository.SalesComplainRepository;

@Service
public class SalesComplainServiceImpl implements SalesComplainService {

        @Autowired
        SalesComplainRepository salesComplainRepository;

        @Override
        public List<SalesComplain> getComplains() {
                return IterableUtils.toList(salesComplainRepository.findAll());
        }

        @Override
        @Transactional(rollbackFor = Exception.class)
        public SalesComplain addOrUpdateComplain(SalesComplain salesComplain) {
                return salesComplainRepository.save(salesComplain);
        }

        @Override
        public List<SalesComplain> getComplainsByTiersId(Integer idTiers) {
                List<SalesComplain> complain = salesComplainRepository.findByTiersId(idTiers);
                if (complain.size() > 0)
                        return complain;
                return null;
        }
}
