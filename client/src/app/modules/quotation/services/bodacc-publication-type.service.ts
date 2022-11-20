import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BodaccPublicationType } from '../../quotation/model/BodaccPublicationType';

@Injectable({
  providedIn: 'root'
})
export class BodaccPublicationTypeService extends AppRestService<BodaccPublicationType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBodaccPublicationTypes() {
    return this.getListCached(new HttpParams(), "bodacc-publication-types");
  }

  addOrUpdateBodaccPublicationType(bodaccPublicationType: BodaccPublicationType) {
    this.clearListCache(new HttpParams(), "bodacc-publication-types");
    return this.addOrUpdate(new HttpParams(), "bodacc-publication-type", bodaccPublicationType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
