import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { City } from '../../miscellaneous/model/City';
import { Country } from '../model/Country';

@Injectable({
  providedIn: 'root'
})
export class CityService extends AppRestService<City>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCities() {
    return this.getListCached(new HttpParams(), "cities");
  }

  addOrUpdateCity(city: City) {
    this.clearListCache(new HttpParams(), "cities");
    return this.addOrUpdate(new HttpParams(), "city", city, "Enregistré", "Erreur lors de l'enregistrement");
  }

  getCitiesFilteredByPostalCode(postalCode: string) {
    return this.getList(new HttpParams().set("postalCode", postalCode), "cities/search/postal-code");
  }

  getCitiesFilteredByCountryAndName(value: string, country: Country | undefined) {
    if (country != undefined && country != null)
      return this.getList(new HttpParams().set("countryId", country.id!).set("city", value), "cities/search/country");
    return this.getList(new HttpParams().set("city", value), "cities/search/country");
  }

}
