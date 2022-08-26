import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Vat } from '../../miscellaneous/model/Vat';

@Injectable({
  providedIn: 'root'
})
export class VatService extends AppRestService<Vat>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getVats() {
    return this.getList(new HttpParams(), "vats");
  }

  addOrUpdateVat(vat: Vat) {
    return this.addOrUpdate(new HttpParams(), "vat", vat, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
