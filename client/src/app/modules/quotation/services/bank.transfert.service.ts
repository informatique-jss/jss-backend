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
  
   addOrUpdateBankTransfert(bankTransfert: BankTransfert) {
    return this.addOrUpdate(new HttpParams(), "bank-transfert", bankTransfert, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
