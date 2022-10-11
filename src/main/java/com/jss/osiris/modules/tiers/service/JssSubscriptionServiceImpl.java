package com.jss.osiris.modules.tiers.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.tiers.model.JssSubscription;
import com.jss.osiris.modules.tiers.repository.JssSubscriptionRepository;

@Service
public class JssSubscriptionServiceImpl implements JssSubscriptionService {

    @Autowired
    JssSubscriptionRepository jssSubscriptionRepository;

    @Override
    public JssSubscription getJssSubscription(Integer id) {
        Optional<JssSubscription> jssSubscription = jssSubscriptionRepository.findById(id);
        if (jssSubscription.isPresent())
            return jssSubscription.get();
        return null;
    }
}
