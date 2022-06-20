import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Siret } from '../model/Siret';

@Injectable({
  providedIn: 'root'
})
export class SiretService extends AppRestService<Siret>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getSiret(siret: string) {
    return this.getList(new HttpParams().set("siret", siret), "siret");
  }
}
