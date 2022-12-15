package com.jss.osiris.libs.ofx;
/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import java.time.LocalDate;
import java.util.List;

public record BankTransactionList(LocalDate dateStart,
        LocalDate dateEnd,
        List<StatementTransaction> transactions) {

    public BankTransactionList() {
        this(LocalDate.now(), LocalDate.now(), List.of());
    }

    public boolean isEmpty() {
        return transactions.isEmpty();
    }
}
