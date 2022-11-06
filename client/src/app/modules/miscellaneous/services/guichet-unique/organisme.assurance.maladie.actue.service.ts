import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OrganismeAssuranceMaladieActue } from 'src/app/modules/quotation/model/guichet-unique/referentials/OrganismeAssuranceMaladieActue';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class OrganismeAssuranceMaladieActueService extends AppRestService<OrganismeAssuranceMaladieActue>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getOrganismeAssuranceMaladieActue() {
    return this.getList(new HttpParams(), 'organisme-assurance-maladie-actue');
  }

}                        
