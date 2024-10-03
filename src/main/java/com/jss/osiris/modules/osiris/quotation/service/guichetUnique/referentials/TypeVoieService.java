package com.jss.osiris.modules.osiris.quotation.service.guichetUnique.referentials;

import java.util.List;

import com.jss.osiris.modules.osiris.quotation.model.guichetUnique.referentials.TypeVoie;

public interface TypeVoieService {
    public List<TypeVoie> getTypeVoie();

    public TypeVoie getTypeVoie(String code);
}
