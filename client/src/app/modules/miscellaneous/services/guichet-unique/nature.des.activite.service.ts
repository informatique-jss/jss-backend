import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NatureDesActivite } from 'src/app/modules/quotation/model/guichet-unique/referentials/NatureDesActivite';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class NatureDesActiviteService extends AppRestService<NatureDesActivite>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getNatureDesActivite() {
    return this.getList(new HttpParams(), 'nature-des-activite');
  }

}                        
