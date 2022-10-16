package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.modules.accounting.model.AccountingAccount;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.Gift;
import com.jss.osiris.modules.miscellaneous.repository.GiftRepository;

@Service
public class GiftServiceImpl implements GiftService {

    @Autowired
    GiftRepository giftRepository;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    public List<Gift> getGifts() {
        return IterableUtils.toList(giftRepository.findAll());
    }

    @Override
    public Gift getGift(Integer id) {
        Optional<Gift> gift = giftRepository.findById(id);
        if (gift.isPresent())
            return gift.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Gift addOrUpdateGift(Gift gift) throws Exception {
        giftRepository.save(gift);
        AccountingAccount accountingAccount = accountingAccountService
                .generateAccountingAccountsForProduct("Cadeau - " + gift.getLabel());
        gift.setAccountingAccount(accountingAccount);
        return giftRepository.save(gift);
    }

    @Override
    public void decreaseStock(Gift gift) {
        if (gift != null) {
            if (gift.getStock() == null)
                gift.setStock(0);
            gift.setStock(gift.getStock() - 1);
            giftRepository.save(gift);
        }
    }
}
