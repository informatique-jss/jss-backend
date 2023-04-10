import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { toIsoString } from 'src/app/libs/FormatHelper';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingRecord } from '../../accounting/model/AccountingRecord';
import { ITiers } from '../../tiers/model/ITiers';
import { AccountingAccount } from '../model/AccountingAccount';
import { AccountingAccountClass } from '../model/AccountingAccountClass';
import { AccountingJournal } from '../model/AccountingJournal';

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

  getAccountingRecordsByTemporaryOperationId(temporaryOperationId: number) {
    return this.getList(new HttpParams().set("temporaryOperationId", temporaryOperationId), "accounting-records/search/temporary");
  }

  deleteRecordsByTemporaryOperationId(temporaryOperationId: number) {
    return this.delete(new HttpParams().set("temporaryOperationId", temporaryOperationId), "accounting-records/delete/temporary");
  }

  getAccountingRecordsByOperationId(operationId: number) {
    return this.getList(new HttpParams().set("operationId", operationId), "accounting-records/search");
  }

  doCounterPartByOperationId(operationId: number) {
    return this.getList(new HttpParams().set("operationId", operationId), "accounting-records/counter-part");
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

  downloadBillingClosureReceiptV2(tiers: ITiers) {
    this.downloadGet(new HttpParams().set("tiersId", tiers.id), "billing-closure-receipt/download/2");
  }
}
