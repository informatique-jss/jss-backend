package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.LieuDeLiquidation;
import com.jss.osiris.modules.quotation.repository.guichetUnique.LieuDeLiquidationRepository;

@Service
public class LieuDeLiquidationServiceImpl implements LieuDeLiquidationService {

    @Autowired
    LieuDeLiquidationRepository LieuDeLiquidationRepository;

    @Override
    @Cacheable(value = "lieuDeLiquidationList", key = "#root.methodName")
    public List<LieuDeLiquidation> getLieuDeLiquidation() {
        return IterableUtils.toList(LieuDeLiquidationRepository.findAll());
    }
}
