package com.jss.osiris.modules.quotation.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.quotation.model.ProvisionScreenType;
import com.jss.osiris.modules.quotation.repository.ProvisionScreenTypeRepository;

@Service
public class ProvisionScreenTypeServiceImpl implements ProvisionScreenTypeService {

    @Autowired
    ProvisionScreenTypeRepository provisionScreenTypeRepository;

    @Override
    public List<ProvisionScreenType> getProvisionScreenTypes() {
        return IterableUtils.toList(provisionScreenTypeRepository.findAll());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateScreenTypes() {
        updateScreenType(ProvisionScreenType.ANNOUNCEMENT, "Ecran annonces légales");
        updateScreenType(ProvisionScreenType.DOMICILIATION, "Ecran domicilation");
        updateScreenType(ProvisionScreenType.FORMALITE, "Ecran formalité");
        updateScreenType(ProvisionScreenType.STANDARD, "Ecran standard");
    }

    private void updateScreenType(String code, String label) {
        if (getScreenTypeByCode(code) == null) {
            ProvisionScreenType screenType = new ProvisionScreenType();
            screenType.setCode(code);
            screenType.setLabel(label);
            provisionScreenTypeRepository.save(screenType);
        }
    }

    private ProvisionScreenType getScreenTypeByCode(String code) {
        return provisionScreenTypeRepository.findByCode(code);
    }

    @Override
    public ProvisionScreenType getProvisionScreenType(Integer id) {
        Optional<ProvisionScreenType> provisionScreenType = provisionScreenTypeRepository.findById(id);
        if (provisionScreenType.isPresent())
            return provisionScreenType.get();
        return null;
    }
}
