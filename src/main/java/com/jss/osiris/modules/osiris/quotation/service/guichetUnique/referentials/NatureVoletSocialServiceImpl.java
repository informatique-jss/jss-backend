package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.NatureVoletSocial;
import com.jss.osiris.modules.osiris.quotation.repository.guichetUnique.NatureVoletSocialRepository;

@Service
public class NatureVoletSocialServiceImpl implements NatureVoletSocialService {

    @Autowired
    NatureVoletSocialRepository NatureVoletSocialRepository;

    @Override
    public List<NatureVoletSocial> getNatureVoletSocial() {
        return IterableUtils.toList(NatureVoletSocialRepository.findAll());
    }
}
