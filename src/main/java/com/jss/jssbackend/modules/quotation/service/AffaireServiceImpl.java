package com.jss.jssbackend.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.quotation.model.Affaire;
import com.jss.jssbackend.modules.quotation.repository.AffaireRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AffaireServiceImpl implements AffaireService {

    @Autowired
    AffaireRepository affaireRepository;

    @Override
    public List<Affaire> getAffaires() {
        return IterableUtils.toList(affaireRepository.findAll());
    }

    @Override
    public Affaire getAffaire(Integer id) {
        Optional<Affaire> affaire = affaireRepository.findById(id);
        if (!affaire.isEmpty())
            return affaire.get();
        return null;
    }
}
