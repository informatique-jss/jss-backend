import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BankTransfert } from '../../quotation/model/BankTransfert';

@Injectable({
  providedIn: 'root'
})
export class BankTransfertService extends AppRestService<BankTransfert>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBankTransfers() {
    return this.getList(new HttpParams(), "bank-transferts");
  }

  cancelBankTransfert(bankTransfert: BankTransfert) {
    return this.get(new HttpParams().set("idBankTranfert", bankTransfert.id), "bank-transfert/cancel");
  }

  selectBankTransfertForExport(bankTransfert: BankTransfert) {
    return this.get(new HttpParams().set("idBankTranfert", bankTransfert.id), "bank-transfert/export/select");
  }

  unselectBankTransfertForExport(bankTransfert: BankTransfert) {
    return this.get(new HttpParams().set("idBankTranfert", bankTransfert.id), "bank-transfert/export/unselect");
  }

  addOrUpdateTransfertComment(comment: string, bankTransfertId: number) {
    return this.postItem(new HttpParams().set("idBankTransfert", bankTransfertId), "bank-transfert/comment", comment);
  }
}
