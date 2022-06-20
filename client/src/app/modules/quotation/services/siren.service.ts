import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Siren } from '../model/Siren';

@Injectable({
  providedIn: 'root'
})
export class SirenService extends AppRestService<Siren>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getSiren(siren: string) {
    return this.getList(new HttpParams().set("siren", siren), "siren");
  }
}
