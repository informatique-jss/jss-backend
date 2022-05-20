import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Country } from '../../miscellaneous/model/Country';

@Injectable({
  providedIn: 'root'
})
export class CountryService extends AppRestService<Country>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getCountries() {
    return this.getList(new HttpParams(), "countries");
  }

}
