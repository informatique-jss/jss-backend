import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DiscoveringOrigin } from '../../tiers/model/DiscoveringOrigin';

@Injectable({
  providedIn: 'root'
})
export class DiscoveringOriginService extends AppRestService<DiscoveringOrigin>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getDiscoveringOrigins() {
    return this.getList(new HttpParams(), "dicovering-origins");
  }
  
   addOrUpdateDiscoveringOrigin(discoveringOrigin: DiscoveringOrigin) {
    return this.addOrUpdate(new HttpParams(), "discovering-origin", discoveringOrigin, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
