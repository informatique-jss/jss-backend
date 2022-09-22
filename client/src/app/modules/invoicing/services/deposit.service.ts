import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Deposit } from '../../invoicing/model/Deposit';

@Injectable({
  providedIn: 'root'
})
export class DepositService extends AppRestService<Deposit>{

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }
}
