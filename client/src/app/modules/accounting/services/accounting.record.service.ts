import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { AccountingRecord } from '../../accounting/model/AccountingRecord';

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

}
