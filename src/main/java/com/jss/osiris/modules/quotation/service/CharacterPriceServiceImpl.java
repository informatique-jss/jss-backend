package com.jss.osiris.modules.quotation.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public CharacterPrice getCharacterPrice(Integer id) {
        Optional<CharacterPrice> characterPrice = characterPriceRepository.findById(id);
        if (characterPrice.isPresent())
            return characterPrice.get();
        return null;
    }

    @Override
    public CharacterPrice getCharacterPrice(Department department, LocalDate date) {
        List<CharacterPrice> characterPrices = IterableUtils.toList(characterPriceRepository.findAll());
        if (characterPrices != null) {
            characterPrices.sort(new Comparator<CharacterPrice>() {
                @Override
                public int compare(CharacterPrice o1, CharacterPrice o2) {
                    return o2.getStartDate().compareTo(o1.getStartDate());
                }
            });
            for (CharacterPrice characterPrice : characterPrices) {
                if (characterPrice.getDepartments().size() > 0)
                    for (Department d : characterPrice.getDepartments()) {
                        if (d.getId() == department.getId() && date.isAfter(characterPrice.getStartDate()))
                            return characterPrice;
                    }
            }
        }
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CharacterPrice addOrUpdateCharacterPrice(
            CharacterPrice characterPrice) {
        return characterPriceRepository.save(characterPrice);
    }

    @Override
    public int getCharacterNumber(Provision provision) {
        if (provision.getAnnouncement() != null) {
            int noticeNumber = 0;
            int headerNumber = 0;
            if (provision.getAnnouncement().getNotice() != null)
                noticeNumber = provision.getAnnouncement().getNotice().replaceAll("\\r|\\n|\\r\\n", " ")
                        .replaceAll("\\<.*?>", "")
                        .length();
            if (provision.getAnnouncement().getNoticeHeader() != null)
                headerNumber = provision.getAnnouncement().getNoticeHeader().replaceAll("\\r|\\n|\\r\\n", " ")
                        .replaceAll("\\<.*?>", "")
                        .length();

            return noticeNumber
                    + ((provision.getAnnouncement().getIsHeaderFree() == null
                            || provision.getAnnouncement().getIsHeaderFree()) ? 0
                                    : headerNumber);
        }
        return 0;
    }

    @Override
    public CharacterPrice getCharacterPrice(Provision provision) {
        if (provision.getAnnouncement() != null && provision.getAnnouncement().getDepartment() != null
                && provision.getAnnouncement().getPublicationDate() != null)
            return this.getCharacterPrice(provision.getAnnouncement().getDepartment(),
                    provision.getAnnouncement().getPublicationDate());
        return null;
    }
}