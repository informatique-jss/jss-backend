import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { SuspiciousInvoiceResult } from '../model/SuspiciousInvoice';

@Injectable({
  providedIn: 'root'
})
export class SuspiciousInvoiceResultService extends AppRestService<SuspiciousInvoiceResult> {

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getSuspiciousInvoiceByTiers(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "suspicious-invoices/tiers");
  }

  getSuspiciousInvoice(accountingDate: Date) {
    return this.getList(new HttpParams().set("accountingDate", toIsoString(accountingDate)), "suspicious-invoices");
  }

}
