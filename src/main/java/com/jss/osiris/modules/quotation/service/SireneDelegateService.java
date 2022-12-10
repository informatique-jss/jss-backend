package com.jss.osiris.modules.quotation.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisClientMessageException;
import com.jss.osiris.modules.quotation.model.Siren;
import com.jss.osiris.modules.quotation.model.Siret;

public interface SireneDelegateService {
    public List<Siren> getSiren(String siren) throws OsirisClientMessageException;

    public List<Siret> getSiret(String siret) throws OsirisClientMessageException;
}
