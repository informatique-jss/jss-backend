package com.jss.jssbackend.modules.tiers.service;

import java.util.List;
import java.util.Optional;

import com.jss.jssbackend.modules.tiers.model.Phone;
import com.jss.jssbackend.modules.tiers.repository.PhoneRepository;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PhoneServiceImpl implements PhoneService {

    @Autowired
    PhoneRepository phoneRepository;

    @Override
    public List<Phone> findPhones(String phone) {
        return IterableUtils
                .toList(phoneRepository.findByPhoneNumberContainingIgnoreCase(phone));
    }

    @Override
    public Phone getPhone(Integer id) {
        Optional<Phone> phone = phoneRepository.findById(id);
        if (!phone.isEmpty())
            return phone.get();
        return null;
    }
}
