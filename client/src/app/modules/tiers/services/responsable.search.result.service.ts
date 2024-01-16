import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { ResponsableSearchResult } from '../model/ResponsableSearchResult';
import { TiersSearch } from '../model/TiersSearch';

@Injectable({
  providedIn: 'root'
})
export class ResponsableSearchResultService extends AppRestService<ResponsableSearchResult>{
  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getResponsableSearch(tiersSearch: TiersSearch) {
    return this.postList(new HttpParams(), "responsable/search", tiersSearch);
  }
}
