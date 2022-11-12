package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.NatureVoletSocial;
import com.jss.osiris.modules.quotation.repository.guichetUnique.NatureVoletSocialRepository;

@Service
public class NatureVoletSocialServiceImpl implements NatureVoletSocialService {

    @Autowired
    NatureVoletSocialRepository NatureVoletSocialRepository;

    @Override
    @Cacheable(value = "natureVoletSocialList", key = "#root.methodName")
    public List<NatureVoletSocial> getNatureVoletSocial() {
        return IterableUtils.toList(NatureVoletSocialRepository.findAll());
    }
}
