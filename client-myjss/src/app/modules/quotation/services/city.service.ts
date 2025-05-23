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

  getCitiesFilteredByNameAndCountryAndPostalCode(name: string, country: Country, postalCode: string, page: number, pageSize: number) {
    let params = new HttpParams();
    params = params.set("name", name!);
    params = params.set("countryId", country.id!);
    params = params.set("postalCode", postalCode);
    params = params.set("page", page);
    params = params.set("size", pageSize);

    return this.getPagedList(params, "cities/search/name/country/postal-code");
  }

  getCitiesByCountry(country: Country) {
    let params = new HttpParams();
    params = params.set("countryId", country.id!);

    return this.getList(params, "cities/search/country");
  }

  getCitiesByPostalCode(postalCode: string) {
    let params = new HttpParams();
    params = params.set("postalCode", postalCode);

    return this.getList(params, "cities/search/postal-code");
  }

}
