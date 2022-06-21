package com.jss.osiris.modules.quotation.service;

import java.util.List;

import org.springframework.web.client.HttpStatusCodeException;

import com.jss.osiris.modules.quotation.model.Rna;

public interface RnaDelegateService {
    public List<Rna> getRna(String rna) throws HttpStatusCodeException, Exception;
}
