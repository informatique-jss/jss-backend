import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { TiersCategory } from '../../profile/model/TiersCategory';

@Injectable({
  providedIn: 'root'
})
export class TiersCategoryService extends AppRestService<TiersCategory> {

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersCategories() {
    return this.getList(new HttpParams(), "tiers-categories");
  }

}
