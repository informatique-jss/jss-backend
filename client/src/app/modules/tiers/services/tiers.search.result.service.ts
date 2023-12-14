import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TiersSearch } from '../model/TiersSearch';
import { TiersSearchResult } from '../model/TiersSearchResult';

@Injectable({
  providedIn: 'root'
})
export class TiersSearchResultService extends AppRestService<TiersSearchResult>{
  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersSearch(tiersSearch: TiersSearch) {
    return this.postList(new HttpParams(), "search", tiersSearch);
  }
}
