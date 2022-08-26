import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Country } from '../../tiers/model/Country';

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

  addOrUpdateCountry(country: Country) {
    return this.addOrUpdate(new HttpParams(), "country", country, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
