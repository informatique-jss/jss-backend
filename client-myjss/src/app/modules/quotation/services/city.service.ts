import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { City } from '../../profile/model/City';
import { Country } from '../../profile/model/Country';

@Injectable({
  providedIn: 'root'
})
export class CityService extends AppRestService<City> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCitiesFilteredByCountryAndNameAndPostalCode(country: Country, postalCode: string) {
    let params = new HttpParams();
    params = params.set("countryId", country.id!);
    params = params.set("postalCode", postalCode);

    return this.getList(params, "cities/search/country/postal-code");
  }

  getCitiesByCountry(country: Country) {
    let params = new HttpParams();
    params = params.set("countryId", country.id!);

    return this.getList(params, "cities/search/country");
  }

}
