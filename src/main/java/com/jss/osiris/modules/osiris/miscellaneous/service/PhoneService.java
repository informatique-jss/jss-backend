package com.jss.osiris.modules.osiris.miscellaneous.service;

import java.util.List;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.osiris.miscellaneous.model.Phone;
import com.jss.osiris.modules.osiris.miscellaneous.model.PhoneSearch;

public interface PhoneService {
    public Phone getPhone(Integer id);

    public List<Phone> populatePhoneIds(List<Phone> phones);

    public List<PhoneSearch> getByPhoneNumber(String phone) throws OsirisException;

}
