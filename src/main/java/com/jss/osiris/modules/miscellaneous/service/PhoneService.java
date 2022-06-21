package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Phone;

public interface PhoneService {
    public List<Phone> findPhones(String phone);

    public Phone getPhone(Integer id);

    public void populateMPhoneIds(List<Phone> phones);
}
