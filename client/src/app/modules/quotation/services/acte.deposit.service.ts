import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ActeDeposit } from '../../quotation/model/ActeDeposit';

@Injectable({
  providedIn: 'root'
})
export class ActeDepositService extends AppRestService<ActeDeposit>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }
}
