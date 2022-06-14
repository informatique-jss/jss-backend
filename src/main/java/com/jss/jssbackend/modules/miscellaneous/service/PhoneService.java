package com.jss.jssbackend.modules.miscellaneous.service;

import java.util.List;

import com.jss.jssbackend.modules.miscellaneous.model.Phone;

public interface PhoneService {
    public List<Phone> findPhones(String phone);

    public Phone getPhone(Integer id);

    public void populateMPhoneIds(List<Phone> phones);
}
