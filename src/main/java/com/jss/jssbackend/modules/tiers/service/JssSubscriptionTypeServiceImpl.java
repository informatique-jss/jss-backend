package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.JssSubscriptionType;
import com.jss.jssbackend.modules.tiers.repository.JssSubscriptionTypeRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JssSubscriptionTypeServiceImpl implements JssSubscriptionTypeService {

    @Autowired
    JssSubscriptionTypeRepository jssSubscriptionTypeRepository;

    @Override
    public List<JssSubscriptionType> getJssSubscriptionTypes() {
        return IterableUtils.toList(jssSubscriptionTypeRepository.findAll());
    }

    @Override
    public JssSubscriptionType getJssSubscriptionType(Integer id) {
        Optional<JssSubscriptionType> jssSubscriptionType = jssSubscriptionTypeRepository.findById(id);
        if (!jssSubscriptionType.isEmpty())
            return jssSubscriptionType.get();
        return null;
    }
}
