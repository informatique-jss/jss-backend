package com.jss.jssbackend.modules.quotation.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.jssbackend.modules.miscellaneous.model.Department;
import com.jss.jssbackend.modules.quotation.model.CharacterPrice;
import com.jss.jssbackend.modules.quotation.repository.CharacterPriceRepository;

@Service
public class CharacterPriceServiceImpl implements CharacterPriceService {

    @Autowired
    CharacterPriceRepository characterPriceRepository;

    @Override
    public List<CharacterPrice> getCharacterPrices() {
        return IterableUtils.toList(characterPriceRepository.findAll());
    }

    @Override
    public CharacterPrice getCharacterPrice(Integer id) {
        Optional<CharacterPrice> characterPrice = characterPriceRepository.findById(id);
        if (!characterPrice.isEmpty())
            return characterPrice.get();
        return null;
    }

    @Override
    public CharacterPrice getCharacterPrice(Department department, Date date) {
        List<CharacterPrice> characterPrices = IterableUtils.toList(characterPriceRepository.findAll());
        for (CharacterPrice characterPrice : characterPrices) {
            if (characterPrice.getDepartments().size() > 0)
                for (Department d : characterPrice.getDepartments()) {
                    if (d.getId() == department.getId() && date.after(characterPrice.getStartDate()))
                        return characterPrice;
                }
        }
        return null;
    }

}
