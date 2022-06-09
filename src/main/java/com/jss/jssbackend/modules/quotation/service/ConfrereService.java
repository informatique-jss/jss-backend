package com.jss.jssbackend.modules.quotation.service;

import java.util.List;

import com.jss.jssbackend.modules.quotation.model.Confrere;

public interface ConfrereService {
    public List<Confrere> getConfreres();

    public Confrere getConfrere(Integer id);
}
