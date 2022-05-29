import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { EntityType } from 'src/app/routing/search/EntityType';
import { Audit } from '../../miscellaneous/model/Audit';

@Injectable({
  providedIn: 'root'
})
export class AuditService extends AppRestService<Audit>{

  constructor(http: HttpClient) {
    super(http, "audit");
  }

  getAuditForEntity(entityId: number, entityType: EntityType) {
    return this.getList(new HttpParams().set("entityType", entityType.entityType).set("entityId", entityId), "search");
  }
}
