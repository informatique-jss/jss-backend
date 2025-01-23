import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Country } from '../../profile/model/Country';

@Injectable({
  providedIn: 'root'
})
export class CountryService extends AppRestService<Country> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCountries() {
    return this.getListCached(new HttpParams(), "countries");
  }
}
