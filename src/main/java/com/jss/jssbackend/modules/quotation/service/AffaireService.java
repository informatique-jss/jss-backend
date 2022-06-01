package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.Affaire;

public interface AffaireService {
    public List<Affaire> getAffaires();

    public Affaire getAffaire(Integer id);
}
