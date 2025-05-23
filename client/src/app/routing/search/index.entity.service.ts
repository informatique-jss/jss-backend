import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { EntityType } from './EntityType';
import { IndexEntity } from './IndexEntity';

@Injectable({
  providedIn: 'root'
})
export class IndexEntityService extends AppRestService<IndexEntity> {

  constructor(http: HttpClient) {
    super(http, "search");
  }

  searchEntities(search: string) {
    return this.getList(new HttpParams().set("search", search), "search");
  }

  searchEntitiesByType(search: string, entityType: EntityType) {
    return this.getList(new HttpParams().set("search", search).set("entityType", entityType.entityType), "search/type");
  }

  getResponsableByKeyword(searchedValue: string, onlyActive: boolean) {
    return this.getList(new HttpParams().set("searchedValue", searchedValue).set("onlyActive", onlyActive), "responsable/search");
  }

  getCustomerOrdersByKeyword(searchedValue: string) {
    return this.getList(new HttpParams().set("searchedValue", searchedValue), "customer/order/search");
  }

  getIndividualTiersByKeyword(searchedValue: string) {
    return this.getList(new HttpParams().set("searchedValue", searchedValue), "individual/search");
  }

  getPostByKeyword(searchedValue: string) {
    return this.getList(new HttpParams().set("searchedValue", searchedValue), "post/search");
  }


}
