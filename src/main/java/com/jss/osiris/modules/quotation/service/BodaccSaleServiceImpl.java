package com.jss.osiris.modules.quotation.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.BodaccSale;
import com.jss.osiris.modules.quotation.repository.BodaccSaleRepository;

@Service
public class BodaccSaleServiceImpl implements BodaccSaleService {

    @Autowired
    BodaccSaleRepository bodaccSaleRepository;

    @Override
    public BodaccSale getBodaccSale(Integer id) {
        Optional<BodaccSale> bodaccSale = bodaccSaleRepository.findById(id);
        if (!bodaccSale.isEmpty())
            return bodaccSale.get();
        return null;
    }
}
