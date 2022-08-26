import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Provider } from '../../miscellaneous/model/Provider';

@Injectable({
  providedIn: 'root'
})
export class ProviderService extends AppRestService<Provider>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getProviders() {
    return this.getList(new HttpParams(), "providers");
  }

  addOrUpdateProvider(provider: Provider) {
    return this.addOrUpdate(new HttpParams(), "provider", provider, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
