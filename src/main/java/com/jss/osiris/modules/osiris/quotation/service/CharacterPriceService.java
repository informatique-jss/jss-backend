package com.jss.osiris.modules.osiris.quotation.service;

import java.time.LocalDate;
import java.util.List;

import com.jss.osiris.modules.osiris.miscellaneous.model.Department;
import com.jss.osiris.modules.osiris.quotation.model.CharacterPrice;
import com.jss.osiris.modules.osiris.quotation.model.Provision;

public interface CharacterPriceService {
    public List<CharacterPrice> getCharacterPrices();

    public CharacterPrice getCharacterPrice(Department department, LocalDate date);

    public CharacterPrice addOrUpdateCharacterPrice(CharacterPrice characterPrice);

    public int getCharacterNumber(Provision provision, boolean ignoreHeaderFree);

    public CharacterPrice getCharacterPrice(Provision provision);

    public CharacterPrice getCharacterPrice(Integer id);

    public CharacterPrice getCharacterPriceFromUser(Department department, LocalDate date);
}
