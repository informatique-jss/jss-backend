package com.jss.osiris.modules.quotation.service.guichetUnique.referentials;

import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.quotation.model.guichetUnique.referentials.MotifRejetCma;
import com.jss.osiris.modules.quotation.repository.guichetUnique.MotifRejetCmaRepository;

@Service
public class MotifRejetCmaServiceImpl implements MotifRejetCmaService {

    @Autowired
    MotifRejetCmaRepository MotifRejetCmaRepository;

    @Override
    public List<MotifRejetCma> getMotifRejetCma() {
        return IterableUtils.toList(MotifRejetCmaRepository.findAll());
    }
}
