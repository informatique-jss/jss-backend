package com.jss.osiris.libs.ofx;
/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

public record CreditCardStatement(String currency,
        AccountInfo creditCardAccountFrom,
        BankTransactionList bankTransactionList,
        PendingTransactionList pendingTransactionList) {
}
