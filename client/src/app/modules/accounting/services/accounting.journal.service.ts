import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingJournal } from '../../accounting/model/AccountingJournal';

@Injectable({
  providedIn: 'root'
})
export class AccountingJournalService extends AppRestService<AccountingJournal>{

  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  getAccountingJournals() {
    return this.getList(new HttpParams(), "accounting-journals");
  }

  addOrUpdateAccountingJournal(accountingJournal: AccountingJournal) {
    return this.addOrUpdate(new HttpParams(), "accounting-journal", accountingJournal, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
