import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { TiersCategory } from '../model/TiersCategory';
import { TiersType } from '../model/TiersType';

@Injectable({
  providedIn: 'root'
})
export class TiersCategoryService extends AppRestService<TiersCategory>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersCategories() {
    return this.getList(new HttpParams(), "categories");
  }

}
