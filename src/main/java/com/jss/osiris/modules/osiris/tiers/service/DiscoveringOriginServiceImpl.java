package com.jss.osiris.modules.osiris.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.osiris.modules.osiris.tiers.model.DiscoveringOrigin;
import com.jss.osiris.modules.osiris.tiers.repository.DiscoveringOriginRepository;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DiscoveringOriginServiceImpl implements DiscoveringOriginService {

    @Autowired
    DiscoveringOriginRepository discoveringOriginRepository;

    @Override
    public List<DiscoveringOrigin> getDiscoveringOrigins() {
        return IterableUtils.toList(discoveringOriginRepository.findAll());
    }

    @Override
    public DiscoveringOrigin getDiscoveringOrigin(Integer id) {
        Optional<DiscoveringOrigin> discoveringOrigin = discoveringOriginRepository.findById(id);
        if (discoveringOrigin.isPresent())
            return discoveringOrigin.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DiscoveringOrigin addOrUpdateDiscoveringOrigin(
            DiscoveringOrigin discoveringOrigin) {
        return discoveringOriginRepository.save(discoveringOrigin);
    }
}
