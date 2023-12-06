package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.FormeJuridique;
import com.jss.osiris.modules.quotation.repository.guichetUnique.FormeJuridiqueRepository;

@Service
public class FormeJuridiqueServiceImpl implements FormeJuridiqueService {

    @Autowired
    FormeJuridiqueRepository FormeJuridiqueRepository;

    @Override
    public List<FormeJuridique> getFormeJuridique() {
        return IterableUtils.toList(FormeJuridiqueRepository.findAll());
    }

    @Override
    public FormeJuridique getFormeJuridique(String code) {
        Optional<FormeJuridique> formeJuridique = FormeJuridiqueRepository.findById(code);
        if (formeJuridique.isPresent())
            return formeJuridique.get();
        return null;
    }
}
