import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { ProvisionDto } from '../model/ProvisionDto';
import { ProvisionSearch } from '../model/ProvisionSearch';

@Injectable({
  providedIn: 'root'
})
export class ProvisionService extends AppRestService<ProvisionDto> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  searchProvisions(provisionSearch: ProvisionSearch) {
    return this.postList(new HttpParams(), "provision/search/v2", provisionSearch);
  }
}