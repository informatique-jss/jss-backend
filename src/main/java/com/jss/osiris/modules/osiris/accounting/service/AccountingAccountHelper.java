package com.jss.osiris.modules.osiris.accounting.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.jss.osiris.modules.osiris.accounting.model.AccountingAccount;

@Service
public class AccountingAccountHelper {

    public static String computeAccountingAccountNumber(AccountingAccount account) {

        int nbZerosToAdd = 8 - (account.getPrincipalAccountingAccount().getCode().length()
                + account.getAccountingAccountSubNumber().toString().length());
        if (nbZerosToAdd <= 0) {
            return account.getPrincipalAccountingAccount().getCode() + account.getAccountingAccountSubNumber();
        } else {
            return account.getPrincipalAccountingAccount().getCode()
                    + StringUtils.leftPad(account.getAccountingAccountSubNumber().toString(),
                            account.getAccountingAccountSubNumber().toString().length() + nbZerosToAdd, '0');
        }

    }
}
