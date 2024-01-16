package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeExerciceActivitePrincipal;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormeExerciceActivitePrincipalRepository;

@Service
public class FormeExerciceActivitePrincipalServiceImpl implements FormeExerciceActivitePrincipalService {

    @Autowired
    FormeExerciceActivitePrincipalRepository FormeExerciceActivitePrincipalRepository;

    @Override
    public List<FormeExerciceActivitePrincipal> getFormeExerciceActivitePrincipal() {
        return IterableUtils.toList(FormeExerciceActivitePrincipalRepository.findAll());
    }

    @Override
    public FormeExerciceActivitePrincipal getFormeExerciceActivitePrincipal(String code) {
        Optional<FormeExerciceActivitePrincipal> formeExerciceActivitePrincipal = FormeExerciceActivitePrincipalRepository
                .findById(code);
        if (formeExerciceActivitePrincipal.isPresent())
            return formeExerciceActivitePrincipal.get();
        return null;
    }
}
