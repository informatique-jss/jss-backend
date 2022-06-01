package com.jss.jssbackend.modules.quotation.service;

import com.jss.jssbackend.modules.quotation.model.Siren;
import com.jss.jssbackend.modules.quotation.model.Siret;

import org.springframework.web.client.HttpStatusCodeException;

public interface SireneDelegateService {
    public Siren getSiren(String siren) throws HttpStatusCodeException, Exception;

    public Siret getSiret(String siret) throws HttpStatusCodeException, Exception;
}
