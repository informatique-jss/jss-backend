package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.Perimetre;
import com.jss.osiris.modules.quotation.repository.guichetUnique.PerimetreRepository;

@Service
public class PerimetreServiceImpl implements PerimetreService {

    @Autowired
    PerimetreRepository PerimetreRepository;

    @Override
    public List<Perimetre> getPerimetre() {
        return IterableUtils.toList(PerimetreRepository.findAll());
    }
}
