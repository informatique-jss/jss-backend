/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */
package com.jss.osiris.libs.ofx;

/**
 * This class represents account information.
 *
 * @param type          account type
 * @param bankId        bank id
 * @param accountNumber account number
 */
public record AccountInfo(Type type,String bankId,String accountNumber){public enum Type {
    NONE,
    CHECKING,
    SAVINGS,
    MONEY_MARKET,
    CREDIT_LINE

    }

    AccountInfo() {
        this(Type.NONE, "", "");
    }
}
