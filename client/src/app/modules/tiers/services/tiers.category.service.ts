import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { TiersCategory } from '../../tiers/model/TiersCategory';

@Injectable({
  providedIn: 'root'
})
export class TiersCategoryService extends AppRestService<TiersCategory>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getTiersCategories() {
    return this.getListCached(new HttpParams(), "tiers-categories");
  }

  addOrUpdateTiersCategory(tiersCategory: TiersCategory) {
    this.clearListCache(new HttpParams(), "tiers-categories");
    return this.addOrUpdate(new HttpParams(), "tiers-category", tiersCategory, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
