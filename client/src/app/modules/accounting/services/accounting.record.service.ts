import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingRecord } from '../../accounting/model/AccountingRecord';
import { ITiers } from '../../tiers/model/ITiers';
import { AccountingAccount } from '../model/AccountingAccount';
import { AccountingAccountClass } from '../model/AccountingAccountClass';
import { AccountingJournal } from '../model/AccountingJournal';
import { AccountingRecordSearchResult } from '../model/AccountingRecordSearchResult';

@Injectable({
  providedIn: 'root'
})
export class AccountingRecordService extends AppRestService<AccountingRecord>{
  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  saveManualOperations(accountingRecords: AccountingRecord[]) {
    return this.postList(new HttpParams(), "accounting-records/manual/add", accountingRecords, "Opérations correctement ajoutées", "Erreur lors de l'ajout des opérations");
  }

  exportGrandLivre(accountingClass: AccountingAccountClass, startDate: Date, endDate: Date) {
    this.downloadGet(new HttpParams().set("accountingClassId", accountingClass.id).set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "grand-livre/export");
  }

  exportAllGrandLivre(startDate: Date, endDate: Date) {
    this.downloadGet(new HttpParams().set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "grand-livre/export");
  }

  exportJournal(accountingJournal: AccountingJournal, startDate: Date, endDate: Date) {
    this.downloadGet(new HttpParams().set("accountingJournalId", accountingJournal.id).set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "journal/export");
  }

  exportAccountingAccount(accountingAccount: AccountingAccount, startDate: Date, endDate: Date) {
    this.downloadGet(new HttpParams().set("accountingAccountId", accountingAccount.id!).set("startDate", toIsoString(startDate)).set("endDate", toIsoString(endDate)), "accounting-account/export");
  }

  downloadBillingClosureReceipt(tiers: ITiers) {
    this.downloadGet(new HttpParams().set("tiersId", tiers.id), "billing-closure-receipt/download");
  }

  sendBillingClosureReceipt(tiers: ITiers) {
    return this.get(new HttpParams().set("tiersId", tiers.id), "billing-closure-receipt/send", "Relevé(s) de compte envoyé(s) aux tiers / responsables");
  }

  deleteRecords(accountingRecord: AccountingRecordSearchResult) {
    return this.get(new HttpParams().set("accountingRecordId", accountingRecord.recordId), "accounting-record/delete");
  }

  letterRecordsForAs400(accountingRecords: AccountingRecordSearchResult[]) {
    return this.get(new HttpParams().set("recordIds", accountingRecords.map(value => value.recordId).join(",")), "accounting-record/letter");
  }
}
