package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.repository.PhoneRepository;

@Service
public class PhoneServiceImpl implements PhoneService {

    @Autowired
    PhoneRepository phoneRepository;

    @Override
    public List<Phone> findPhones(String phone) {
        if (phone != null)
            return phoneRepository.findByPhoneNumberContainingIgnoreCase(phone);
        return null;
    }

    @Override
    public Phone getPhone(Integer id) {
        Optional<Phone> phone = phoneRepository.findById(id);
        if (phone.isPresent())
            return phone.get();
        return null;
    }

    @Override
    public List<Phone> populatePhoneIds(List<Phone> phones) {
        if (phones != null)
            for (Phone phone : phones) {
                if (phone.getId() == null) {
                    List<Phone> existingPhones = findPhones(phone.getPhoneNumber());
                    if (existingPhones != null && existingPhones.size() == 1)
                        phone.setId(existingPhones.get(0).getId());
                    phoneRepository.save(phone);
                }
            }
        return phones;
    }
}