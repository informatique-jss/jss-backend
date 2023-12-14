package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;

public interface FormeExerciceActivitePrincipalService {
    public List<FormeExerciceActivitePrincipal> getFormeExerciceActivitePrincipal();

    public FormeExerciceActivitePrincipal getFormeExerciceActivitePrincipal(String code);
}
