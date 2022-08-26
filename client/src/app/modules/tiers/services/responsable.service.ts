import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Responsable } from '../model/Responsable';

@Injectable({
  providedIn: 'root'
})
export class ResponsableService extends AppRestService<Responsable>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getResponsableByKeyword(searchedValue: string) {
    return this.getList(new HttpParams().set("searchedValue", searchedValue), "responsable/search");
  }

}
