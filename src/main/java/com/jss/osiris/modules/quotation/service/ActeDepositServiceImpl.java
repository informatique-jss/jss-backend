package com.jss.osiris.modules.quotation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.Formalite;
import com.jss.osiris.modules.quotation.repository.ActeDepositRepository;

@Service
public class ActeDepositServiceImpl implements ActeDepositService {

    @Autowired
    ActeDepositRepository acteDepositRepository;

    @Override
    public void declareActeDepositOnGuichetUnique(Formalite formalite) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'declareActeDepositOnGuichetUnique'");
    }

}
