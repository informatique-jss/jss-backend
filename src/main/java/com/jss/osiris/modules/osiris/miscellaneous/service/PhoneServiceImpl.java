package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.libs.ValidationHelper;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.miscellaneous.model.PhoneSearch;
import com.jss.osiris.modules.osiris.miscellaneous.repository.PhoneRepository;

@Service
public class PhoneServiceImpl implements PhoneService {

    @Autowired
    PhoneRepository phoneRepository;

    @Autowired
    ValidationHelper validationHelper;

    @Override
    public List<PhoneSearch> getByPhoneNumber(String phoneNumber) throws OsirisException {
        if (validationHelper.validateFrenchPhone(phoneNumber) && phoneNumber.startsWith("+33"))
            phoneNumber = phoneNumber.replace("+33", "0");
        phoneNumber.replaceAll(" ", "");
        return phoneRepository.findByPhoneNumberForFront(phoneNumber);
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
                    List<Phone> existingPhones = phoneRepository.findByPhoneNumber(phone.getPhoneNumber());
                    if (existingPhones != null && existingPhones.size() == 1)
                        phone.setId(existingPhones.get(0).getId());
                    phoneRepository.save(phone);
                }
            }
        return phones;
    }
}