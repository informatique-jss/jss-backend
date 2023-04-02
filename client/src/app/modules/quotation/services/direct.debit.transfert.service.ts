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

  cancelDirectDebitTransfert(directDebitTransfert: DirectDebitTransfert) {
    return this.get(new HttpParams().set("idDirectDebitTranfert", directDebitTransfert.id), "direct-debit-transfert/cancel");
  }
}
