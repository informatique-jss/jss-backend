import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { BodaccPublicationType } from '../../quotation/model/BodaccPublicationType';

@Injectable({
  providedIn: 'root'
})
export class BodaccPublicationTypeService extends AppRestService<BodaccPublicationType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBodaccPublicationTypes() {
    return this.getList(new HttpParams(), "bodacc-publication-types");
  }

  addOrUpdateBodaccPublicationType(bodaccPublicationType: BodaccPublicationType) {
    return this.addOrUpdate(new HttpParams(), "bodacc-publication-type", bodaccPublicationType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
