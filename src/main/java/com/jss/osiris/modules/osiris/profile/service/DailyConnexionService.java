package com.jss.osiris.modules.osiris.profile.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.profile.model.DailyConnexion;
import com.jss.osiris.modules.osiris.tiers.model.Responsable;

public interface DailyConnexionService {

    List<DailyConnexion> getDailyConnexions();

    DailyConnexion getDailyConnexion(Integer id);

    DailyConnexion addOrUpdateDailyConnexion(DailyConnexion dailyConnexion);

    List<DailyConnexion> getDailyConnexionsForDay(LocalDate day);

    void declareConnexionForToday(Responsable responsable);

}
