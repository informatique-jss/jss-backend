package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import org.springframework.web.client.HttpStatusCodeException;

import com.jss.jssbackend.modules.quotation.model.Rna;

public interface RnaDelegateService {
    public List<Rna> getRna(String rna) throws HttpStatusCodeException, Exception;
}
