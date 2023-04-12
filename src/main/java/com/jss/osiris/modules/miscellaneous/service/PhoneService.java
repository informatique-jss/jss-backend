package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Phone;
import com.jss.osiris.modules.miscellaneous.model.PhoneTeams;

public interface PhoneService {
    public List<Phone> findPhones(String phone);

    public Phone getPhone(Integer id);

    public List<Phone> populatePhoneIds(List<Phone> phones);

    public List<PhoneTeams> getByTelNumber(String phone) throws OsirisException;
}
