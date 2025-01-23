package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;

public interface FormeExerciceActivitePrincipalService {
    public List<FormeExerciceActivitePrincipal> getFormeExerciceActivitePrincipal();

    public FormeExerciceActivitePrincipal getFormeExerciceActivitePrincipal(String code);
}
