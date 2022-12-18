package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.modules.quotation.model.Affaire;

public interface AffaireService {
    public List<Affaire> getAffaires();

    public Affaire getAffaire(Integer id);

    public Affaire addOrUpdateAffaire(Affaire affaire);

    public void reindexAffaire();

    public Affaire getAffaireBySiret(String siret);

}
