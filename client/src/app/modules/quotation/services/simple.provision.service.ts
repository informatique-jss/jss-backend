import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { SimpleProvision } from '../../quotation/model/SimpleProvision';

@Injectable({
  providedIn: 'root'
})
export class SimpleProvisionService extends AppRestService<SimpleProvision>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getSimpleProvisions() {
    return this.getList(new HttpParams(), "simple-provisions");
  }
  
   addOrUpdateSimpleProvision(simpleProvision: SimpleProvision) {
    return this.addOrUpdate(new HttpParams(), "simple-provision", simpleProvision, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
