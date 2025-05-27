import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { IndexEntity } from '../model/IndexEntity';

@Injectable({
  providedIn: 'root'
})
export class IndexEntityService extends AppRestService<IndexEntity> {

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  globalSearchForEntity(searchText: string) {
    return this.getList(new HttpParams().set("searchText", searchText), "search/global");
  }
}
