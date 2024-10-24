import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class BankBalanceService extends AppRestService<number> {
  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getAccountingRecordBalanceByAccountingAccountId(accountingAccountId: number) {
    return this.get(new HttpParams().set("accountingAccountId", accountingAccountId), "accounting-record/accounting-account-id");
  }

  getBankTransfertTotal() {
    return this.get(new HttpParams(), "accounting-record/bank-transfert-total")
  }

  getRefundTotal() {
    return this.get(new HttpParams(), "accounting-record/refund-total");
  }

  getCheckTotal() {
    return this.get(new HttpParams(), "accounting-record/check-total");
  }

  getDirectDebitTransfertTotal() {
    return this.get(new HttpParams(), "accounting-record/direct-debit-transfert-total");
  }
}
