import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { DebourSearch } from '../../quotation/model/DebourSearch';
import { DebourSearchResult } from '../../quotation/model/DebourSearchResult';

@Injectable({
  providedIn: 'root'
})
export class DebourSearchResultService extends AppRestService<DebourSearchResult>{
  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  getDebours(debourSearch: DebourSearch) {
    return this.postList(new HttpParams(), "debours/search", debourSearch);
  }
}
