import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { Payment } from '../../invoicing/model/Payment';

@Injectable({
  providedIn: 'root'
})
export class BankBalancePaymentService extends AppRestService<Payment> {
  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  // Bank balance
  getBankTransfertList(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/bank-transfert-list")
  }

  getRefundList(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/refund-list");
  }

  getCheckList(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/check-list");
  }

  getDirectDebitTransfertList(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "accounting-record/direct-debit-transfert-list");
  }
}
