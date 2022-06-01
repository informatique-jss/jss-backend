import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { City } from '../../miscellaneous/model/City';
import { Country } from '../model/Country';

@Injectable({
  providedIn: 'root'
})
export class CityService extends AppRestService<City>{
  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getCities() {
    return this.getList(new HttpParams(), "cities");
  }

  getCitiesFilteredByPostalCode(postalCode: string) {
    return this.getList(new HttpParams().set("postalCode", postalCode), "cities/search/postal-code");
  }

  getCitiesFilteredByCountryAndName(value: string, country: Country) {
    if (country != undefined && country != null)
      return this.getList(new HttpParams().set("countryId", country.id).set("city", value), "cities/search/country");
    return this.getList(new HttpParams().set("city", value), "cities/search/country");
  }
}
