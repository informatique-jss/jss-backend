package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import org.springframework.web.client.HttpStatusCodeException;

import com.jss.jssbackend.modules.quotation.model.Siren;
import com.jss.jssbackend.modules.quotation.model.Siret;

public interface SireneDelegateService {
    public List<Siren> getSiren(String siren) throws HttpStatusCodeException, Exception;

    public List<Siret> getSiret(String siret) throws HttpStatusCodeException, Exception;
}
