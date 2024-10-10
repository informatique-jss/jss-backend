import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Responsable } from '../model/Responsable';
import { UserScope } from '../model/UserScope';

@Injectable({
  providedIn: 'root'
})
export class UserScopeService extends AppRestService<UserScope> {

  constructor(http: HttpClient) {
    super(http, "profile");
  }

  getUserScope() {
    return this.getListCached(new HttpParams(), "user/scope");
  }

  addToUsersScope(responsables: Responsable[]) {
    this.clearListCache(new HttpParams(), "user/scope");
    return this.postList(new HttpParams(), "user/scope/add", responsables, "Votre vue d'ensemble a été mise à jour");
  }

  removeFromUserScope(responsable: Responsable) {
    this.clearListCache(new HttpParams(), "user/scope");
    return this.get(new HttpParams().set("idResponsable", responsable.id), "user/scope/remove");
  }

}
