import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IndexEntity } from './IndexEntity';

@Injectable({
  providedIn: 'root'
})
export class IndexEntityService extends AppRestService<IndexEntity>{

  constructor(http: HttpClient) {
    super(http, "search");
  }

  searchEntities(search: string) {
    return this.getList(new HttpParams().set("search", search), "search");
  }

}
