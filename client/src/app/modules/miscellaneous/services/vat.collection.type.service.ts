import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { VatCollectionType } from '../../miscellaneous/model/VatCollectionType';

@Injectable({
  providedIn: 'root'
})
export class VatCollectionTypeService extends AppRestService<VatCollectionType>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getVatCollectionTypes() {
    return this.getList(new HttpParams(), "vat-collection-types");
  }

  addOrUpdateVatCollectionType(vatCollectionType: VatCollectionType) {
    return this.addOrUpdate(new HttpParams(), "vat-collection-type", vatCollectionType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
