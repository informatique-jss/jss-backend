package com.jss.osiris.modules.osiris.profile.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.osiris.profile.model.DailyConnexion;
import com.jss.osiris.modules.osiris.profile.repository.DailyConnexionRepository;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

@Service
public class DailyConnexionServiceImpl implements DailyConnexionService {

    @Autowired
    DailyConnexionRepository dailyConnexionRepository;

    @Override
    public List<DailyConnexion> getDailyConnexions() {
        return IterableUtils.toList(dailyConnexionRepository.findAll());
    }

    @Override
    public DailyConnexion getDailyConnexion(Integer id) {
        Optional<DailyConnexion> dailyConnexion = dailyConnexionRepository.findById(id);
        if (dailyConnexion.isPresent())
            return dailyConnexion.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DailyConnexion addOrUpdateDailyConnexion(DailyConnexion dailyConnexion) {
        return dailyConnexionRepository.save(dailyConnexion);
    }

    @Override
    public void declareConnexionForToday(Responsable responsable) {
        DailyConnexion connexion = dailyConnexionRepository.findByResponsableAndConnexionDate(responsable,
                LocalDate.now());
        if (connexion == null) {
            connexion = new DailyConnexion();
            connexion.setResponsable(responsable);
            connexion.setConnexionDate(LocalDate.now());
            addOrUpdateDailyConnexion(connexion);
        }
    }

    @Override
    public List<DailyConnexion> getDailyConnexionsForDay(LocalDate day) {
        return dailyConnexionRepository.findByConnexionDate(day);
    }
}
