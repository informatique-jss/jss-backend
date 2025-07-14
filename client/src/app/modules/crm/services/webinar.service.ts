import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Webinar } from '../../crm/model/Webinar';

@Injectable({
  providedIn: 'root'
})
export class WebinarService extends AppRestService<Webinar>{

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  getWebinars() {
    return this.getList(new HttpParams(), "webinars");
  }
  
   addOrUpdateWebinar(webinar: Webinar) {
    return this.addOrUpdate(new HttpParams(), "webinar", webinar, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
