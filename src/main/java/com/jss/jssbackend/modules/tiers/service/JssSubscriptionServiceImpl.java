package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.JssSubscription;
import com.jss.jssbackend.modules.tiers.repository.JssSubscriptionRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JssSubscriptionServiceImpl implements JssSubscriptionService {

    @Autowired
    JssSubscriptionRepository jssSubscriptionRepository;

    @Override
    public JssSubscription getJssSubscription(Integer id) {
        Optional<JssSubscription> jssSubscription = jssSubscriptionRepository.findById(id);
        if (!jssSubscription.isEmpty())
            return jssSubscription.get();
        return null;
    }
}
