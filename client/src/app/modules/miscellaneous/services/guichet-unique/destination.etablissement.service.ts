import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { DestinationEtablissement } from 'src/app/modules/quotation/model/guichet-unique/referentials/DestinationEtablissement';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class DestinationEtablissementService extends AppRestService<DestinationEtablissement>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getDestinationEtablissement() {
    return this.getList(new HttpParams(), 'destination-etablissement');
  }

}                        
