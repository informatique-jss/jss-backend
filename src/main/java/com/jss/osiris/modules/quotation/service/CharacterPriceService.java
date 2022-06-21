package com.jss.osiris.modules.quotation.service;

import java.util.Date;
import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.quotation.model.CharacterPrice;

public interface CharacterPriceService {
    public List<CharacterPrice> getCharacterPrices();

    public CharacterPrice getCharacterPrice(Integer id);

    public CharacterPrice getCharacterPrice(Department department, Date date);
}
