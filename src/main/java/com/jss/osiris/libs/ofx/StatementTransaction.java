package com.jss.osiris.libs.ofx;
/*
 Copyright (c) Petr Panteleyev. All rights reserved.
 Licensed under the BSD license. See LICENSE file in the project root for full license information.
 */

import java.math.BigDecimal;
import java.time.LocalDate;

public record StatementTransaction(
        String id,
        TransactionEnum type,
        LocalDate datePosted,
        LocalDate dateUser,
        LocalDate dateAvailable,
        BigDecimal amount,
        String name,
        String memo,
        String checkNum) {

    public static class Builder {
        private String id;
        private TransactionEnum type;
        private LocalDate datePosted;
        private LocalDate dateUser;
        private LocalDate dateAvailable;
        private BigDecimal amount;
        private String name;
        private String memo;
        private String checkNum;

        public StatementTransaction build() {
            return new StatementTransaction(id,
                    type,
                    datePosted,
                    dateUser,
                    dateAvailable,
                    amount,
                    name,
                    memo,
                    checkNum);
        }

        public Builder datePosted(LocalDate datePosted) {
            this.datePosted = datePosted;
            return this;
        }

        public Builder dateUser(LocalDate dateUser) {
            this.dateUser = dateUser;
            return this;
        }

        public Builder dateAvailable(LocalDate dateAvailable) {
            this.dateAvailable = dateAvailable;
            return this;
        }

        Builder id(String id) {
            this.id = id;
            return this;
        }

        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder memo(String memo) {
            this.memo = memo;
            return this;
        }

        Builder amount(String amount) {
            this.amount = new BigDecimal(amount);
            return this;
        }

        Builder type(String type) {
            this.type = TransactionEnum.valueOf(type);
            return this;
        }

        Builder checkNum(String checkNum) {
            this.checkNum = checkNum;
            return this;
        }
    }
}
