package com.jss.osiris.modules.miscellaneous.service;

import java.util.List;
import java.util.Optional;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.accounting.model.AccountingAccountBinome;
import com.jss.osiris.modules.accounting.service.AccountingAccountService;
import com.jss.osiris.modules.miscellaneous.model.BillingType;
import com.jss.osiris.modules.miscellaneous.repository.BillingTypeRepository;

@Service
public class BillingTypeServiceImpl implements BillingTypeService {

    @Autowired
    BillingTypeRepository billingTypeRepository;

    @Autowired
    AccountingAccountService accountingAccountService;

    @Override
    public List<BillingType> getBillingTypes() {
        return IterableUtils.toList(billingTypeRepository.findAll());
    }

    @Override
    public BillingType getBillingType(Integer id) {
        Optional<BillingType> billingType = billingTypeRepository.findById(id);
        if (billingType.isPresent())
            return billingType.get();
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BillingType addOrUpdateBillingType(BillingType billingType) throws OsirisException {
        if (billingType.getId() == null
                || billingType.getAccountingAccountCharge() == null
                        && billingType.getAccountingAccountProduct() == null) {
            AccountingAccountBinome accountingAccounts = accountingAccountService
                    .generateAccountingAccountsForBillingType(billingType);
            billingType.setAccountingAccountCharge(accountingAccounts.getAccountingAccountCharge());
            billingType.setAccountingAccountProduct(accountingAccounts.getAccountingAccountProduct());
        }
        return billingTypeRepository.save(billingType);
    }

    // TOOD : delete !
    @Scheduled(initialDelay = 1000, fixedDelay = 1000000)
    public void upgradeBillingType() throws OsirisException {
        List<BillingType> billingTypes = getBillingTypes();
        if (billingTypes != null)
            for (BillingType billingType : billingTypes)
                addOrUpdateBillingType(billingType);
    }
}
