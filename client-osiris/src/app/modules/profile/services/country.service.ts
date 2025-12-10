import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { Country } from '../../profile/model/Country';

@Injectable({
  providedIn: 'root'
})
export class CountryService extends AppRestService<Country> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCountries() {
    return this.getList(new HttpParams(), "countries");
  }
}
