import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AccountingRecord } from '../../accounting/model/AccountingRecord';
import { AccountingAccountClass } from '../model/AccountingAccountClass';
import { AccountingRecordSearch } from '../model/AccountingRecordSearch';

@Injectable({
  providedIn: 'root'
})
export class AccountingRecordService extends AppRestService<AccountingRecord>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getAccountingRecords() {
    return this.getList(new HttpParams(), "accounting-records");
  }

  addOrUpdateAccountingRecord(accountingRecord: AccountingRecord) {
    return this.addOrUpdate(new HttpParams(), "accounting-record", accountingRecord, "Enregistré", "Erreur lors de l'enregistrement");
  }

  searchAccountingRecords(accountingRecordSearch: AccountingRecordSearch) {
    return this.postList(new HttpParams(), "accounting-record/search", accountingRecordSearch);
  }

  exportGrandLivre(accountingClass: AccountingAccountClass, startDate: Date, endDate: Date) {
    this.downloadGet(new HttpParams().set("accountingClassId", accountingClass.id).set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "grand-livre");
  }
}
