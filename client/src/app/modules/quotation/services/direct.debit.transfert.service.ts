import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DirectDebitTransfert } from '../../quotation/model/DirectDebitTransfert';

@Injectable({
  providedIn: 'root'
})
export class DirectDebitTransfertService extends AppRestService<DirectDebitTransfert>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getDirectDebitTransferts() {
    return this.getList(new HttpParams(), "direct-debit-transferts");
  }
  
   addOrUpdateDirectDebitTransfert(directDebitTransfert: DirectDebitTransfert) {
    return this.addOrUpdate(new HttpParams(), "direct-debit-transfert", directDebitTransfert, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
