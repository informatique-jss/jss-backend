import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { Tiers } from '../model/Tiers';
import { TiersDocumentType } from '../model/TiersDocumentType';
import { TiersType } from '../model/TiersType';

@Injectable({
  providedIn: 'root'
})
export class TiersDocumentTypeService extends AppRestService<TiersDocumentType>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getDocumentTypes() {
    return this.getList(new HttpParams(), "document-types");
  }

}
