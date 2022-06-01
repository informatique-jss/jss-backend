package com.jss.jssbackend.modules.quotation.service;

import com.jss.jssbackend.modules.quotation.model.Rna;

import org.springframework.web.client.HttpStatusCodeException;

public interface RnaDelegateService {
    public Rna getRna(String rna) throws HttpStatusCodeException, Exception;
}
