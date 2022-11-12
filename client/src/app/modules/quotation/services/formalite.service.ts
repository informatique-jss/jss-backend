import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Formalite } from '../model/guichet-unique/Formalite';

@Injectable({
  providedIn: 'root'
})
export class FormaliteService extends AppRestService<Formalite>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }
}
