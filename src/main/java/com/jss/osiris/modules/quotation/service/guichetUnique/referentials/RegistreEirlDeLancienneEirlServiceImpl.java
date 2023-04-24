package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.RegistreEirlDeLancienneEirl;
import com.jss.osiris.modules.quotation.repository.guichetUnique.RegistreEirlDeLancienneEirlRepository;

@Service
public class RegistreEirlDeLancienneEirlServiceImpl implements RegistreEirlDeLancienneEirlService {

    @Autowired
    RegistreEirlDeLancienneEirlRepository RegistreEirlDeLancienneEirlRepository;

    @Override
    public List<RegistreEirlDeLancienneEirl> getRegistreEirlDeLancienneEirl() {
        return IterableUtils.toList(RegistreEirlDeLancienneEirlRepository.findAll());
    }
}
