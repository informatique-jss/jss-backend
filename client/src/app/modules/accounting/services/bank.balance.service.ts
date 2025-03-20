import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class BankBalanceService extends AppRestService<number> {
  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getAccountingRecordBalanceByAccountingAccountId(accountingAccountId: number, accountingDate: Date) {
    return this.get(new HttpParams().set("accountingAccountId", accountingAccountId).set("accountingDate", toIsoString(accountingDate)), "accounting-record/accounting-account-id");
  }

  getBankTransfertTotal(accountingDate: Date) {
    return this.get(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/bank-transfert-total")
  }

  getRefundTotal(accountingDate: Date) {
    return this.get(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/refund-total");
  }

  getCheckTotal(accountingDate: Date) {
    return this.get(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/check-total");
  }

  getCheckInboundTotal(accountingDate: Date) {
    return this.get(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/check-inbound-total");
  }

  getDirectDebitTransfertTotal(accountingDate: Date) {
    return this.get(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/direct-debit-transfert-total");
  }
}
