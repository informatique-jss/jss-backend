import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Country } from '../../miscellaneous/model/Country';

@Injectable({
  providedIn: 'root'
})
export class CountryService extends AppRestService<Country>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getCountries() {
    return this.getListCached(new HttpParams(), "countries");
  }

  addOrUpdateCountry(country: Country) {
    this.clearListCache(new HttpParams(), "countries");
    return this.addOrUpdate(new HttpParams(), "country", country, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
