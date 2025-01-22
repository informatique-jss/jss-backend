import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { AccountingRecord } from '../../accounting/model/AccountingRecord';
import { Responsable } from '../../tiers/model/Responsable';
import { Tiers } from '../../tiers/model/Tiers';
import { AccountingRecordSearch } from '../model/AccountingRecordSearch';
import { AccountingRecordSearchResult } from '../model/AccountingRecordSearchResult';

@Injectable({
  providedIn: 'root'
})
export class AccountingRecordService extends AppRestService<AccountingRecord> {
  constructor(http: HttpClient) {
    super(http, "accounting");
  }

  saveManualOperations(accountingRecords: AccountingRecord[]) {
    return this.postList(new HttpParams(), "accounting-records/manual/add", accountingRecords, "Opérations correctement ajoutées", "Erreur lors de l'ajout des opérations");
  }

  exportGrandLivre(accountingRecordSearch: AccountingRecordSearch) {
    this.downloadPost(new HttpParams(), "grand-livre/export", accountingRecordSearch as any as AccountingRecord);
  }

  exportJournal(accountingRecordSearch: AccountingRecordSearch) {
    this.downloadPost(new HttpParams(), "journal/export", accountingRecordSearch as any as AccountingRecord);
  }

  exportAccountingAccount(accountingRecordSearch: AccountingRecordSearch) {
    this.downloadPost(new HttpParams(), "accounting-account/export", accountingRecordSearch as any as AccountingRecord);
  }

  downloadBillingClosureReceipt(tiers: Tiers, responsable: Responsable | undefined) {
    let params = new HttpParams()
    if (tiers)
      params = params.set("tiersId", tiers.id);
    if (responsable)
      params = params.set("responsableId", responsable.id + "");
    this.downloadGet(params, "billing-closure-receipt/download");
  }

  sendBillingClosureReceipt(tiers: Tiers, responsable: Responsable | undefined) {
    return this.get(new HttpParams().set("tiersId", tiers.id).set("responsableId", responsable ? responsable.id : 'null'), "billing-closure-receipt/send", "Relevé(s) de compte envoyé(s) aux tiers / responsables");
  }

  deleteRecords(accountingRecord: AccountingRecordSearchResult) {
    return this.get(new HttpParams().set("accountingRecordId", accountingRecord.recordId), "accounting-record/delete");
  }

  getAccountingRecordsByTemporaryOperationId(temporaryOperationId: number) {
    return this.getList(new HttpParams().set("temporaryOperationId", temporaryOperationId), "accounting-record/temporary-operation-id");
  }

  letterRecordsForAs400(accountingRecords: AccountingRecordSearchResult[]) {
    return this.get(new HttpParams().set("recordIds", accountingRecords.map(value => value.recordId).join(",")), "accounting-record/letter");
  }
}
