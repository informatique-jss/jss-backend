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

  getCitiesFilteredByCountryAndNameAndPostalCode(value: string, country: Country | undefined, postalCode: string | undefined) {
    let params = new HttpParams();
    if (country)
      params = params.set("countryId", country.id!);
    if (postalCode)
      params = params.set("postalCode", postalCode);
    params = params.set("city", value);

    return this.getList(params, "cities/search/country");
  }

}
