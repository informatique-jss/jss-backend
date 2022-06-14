package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.jssbackend.modules.miscellaneous.model.Phone;
import com.jss.jssbackend.modules.miscellaneous.repository.PhoneRepository;

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

    @Override
    public void populateMPhoneIds(List<Phone> phones) {
        for (Phone phone : phones) {
            if (phone.getId() == null) {
                List<Phone> existingPhones = findPhones(phone.getPhoneNumber());
                if (existingPhones != null && existingPhones.size() == 1)
                    phone.setId(existingPhones.get(0).getId());
            }
        }
    }
}