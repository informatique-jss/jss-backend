package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Provision;

public interface CharacterPriceService {
    public List<CharacterPrice> getCharacterPrices();

    public CharacterPrice getCharacterPrice(Department department, LocalDate date);

    public CharacterPrice addOrUpdateCharacterPrice(CharacterPrice characterPrice);

    public int getCharacterNumber(Provision provision);

    public CharacterPrice getCharacterPrice(Provision provision);
}
