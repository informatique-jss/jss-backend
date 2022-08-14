package com.jss.osiris.modules.quotation.service;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.miscellaneous.model.Department;
import com.jss.osiris.modules.quotation.model.CharacterPrice;
import com.jss.osiris.modules.quotation.model.Provision;
import com.jss.osiris.modules.quotation.repository.CharacterPriceRepository;

@Service
public class CharacterPriceServiceImpl implements CharacterPriceService {

    @Autowired
    CharacterPriceRepository characterPriceRepository;

    @Override
    public List<CharacterPrice> getCharacterPrices() {
        return IterableUtils.toList(characterPriceRepository.findAll());
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

    @Override
    public CharacterPrice addOrUpdateCharacterPrice(
            CharacterPrice characterPrice) {
        return characterPriceRepository.save(characterPrice);
    }

    @Override
    public int getCharacterNumber(Provision provision) {
        if (provision.getShal() != null) {
            int noticeNumber = 0;
            int headerNumber = 0;
            if (provision.getShal().getNotice() != null)
                noticeNumber = provision.getShal().getNotice().replaceAll("\\r|\\n|\\r\\n", " ")
                        .replaceAll("\\<.*?>", "")
                        .length();
            if (provision.getShal().getNoticeHeader() != null)
                headerNumber = provision.getShal().getNoticeHeader().replaceAll("\\r|\\n|\\r\\n", " ")
                        .replaceAll("\\<.*?>", "")
                        .length();

            return noticeNumber
                    + ((provision.getShal().getIsHeaderFree() == null || provision.getShal().getIsHeaderFree()) ? 0
                            : headerNumber);
        }
        return 0;
    }

    @Override
    public CharacterPrice getCharacterPrice(Provision provision) {
        if (provision.getShal() != null && provision.getShal().getDepartment() != null
                && provision.getShal().getPublicationDate() != null)
            return this.getCharacterPrice(provision.getShal().getDepartment(),
                    provision.getShal().getPublicationDate());
        return null;
    }
}