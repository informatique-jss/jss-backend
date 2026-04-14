import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { InformationBanner } from '../../miscellaneous/model/InformationBanner';

@Injectable({
  providedIn: 'root'
})
export class InformationBannerService extends AppRestService<InformationBanner>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getInformationBanners() {
    return this.getList(new HttpParams(), "information-banners");
  }
  
   addOrUpdateInformationBanner(informationBanner: InformationBanner) {
    return this.addOrUpdate(new HttpParams(), "information-banner", informationBanner, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
