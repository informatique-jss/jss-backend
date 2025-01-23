package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.FormeJuridique;

public interface FormeJuridiqueService {
    public List<FormeJuridique> getFormeJuridique();

    public FormeJuridique getFormeJuridique(String code);
}
