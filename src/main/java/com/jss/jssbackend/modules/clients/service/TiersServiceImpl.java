package com.jss.jssbackend.modules.clients.service;

import java.util.Optional;

import com.jss.jssbackend.modules.clients.model.Tiers;
import com.jss.jssbackend.modules.clients.repository.TiersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TiersServiceImpl implements TiersService {

    @Autowired
    TiersRepository tiersRepository;

    @Override
    public Tiers getTiersById(Integer id) {
        Optional<Tiers> tiers = tiersRepository.findById(id);
        if (!tiers.isEmpty())
            return tiers.get();
        return null;
    }

    @Override
    public Tiers addOrUpdateTiers(Tiers tiers) {
        return tiersRepository.save(tiers);
    }
}
