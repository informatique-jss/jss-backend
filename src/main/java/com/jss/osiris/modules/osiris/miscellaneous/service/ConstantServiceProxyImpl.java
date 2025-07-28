package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Constant;
import com.jss.osiris.modules.osiris.miscellaneous.repository.ConstantRepository;

@Service
public class ConstantServiceProxyImpl {

    @Autowired
    ConstantRepository constantRepository;

    LocalDateTime lastFetchedConstant = null;
    Constant cachedConstant = null;

    public Constant getConstants() throws OsirisException {
        List<Constant> constants = IterableUtils.toList(constantRepository.findAll());
        if (constants == null || constants.size() != 1)
            throw new OsirisException(null, "Constants not defined or multiple");
        return constants.get(0);
    }

    @Transactional(rollbackFor = Exception.class)
    public Constant addOrUpdateConstant(Constant constant) throws OsirisException {
        return constantRepository.save(constant);
    }
}
