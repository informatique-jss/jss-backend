import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Provision } from '../../quotation/model/Provision';
import { FormaliteGuichetUnique } from '../../quotation/model/guichet-unique/FormaliteGuichetUnique';

@Injectable({
  providedIn: 'root'
})
export class FormaliteGuichetUniqueService extends AppRestService<FormaliteGuichetUnique>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getFormaliteGuichetUniqueServiceByReference(value: string, provision: Provision) {
    return this.getList(new HttpParams().set("value", value).set("provisionId", provision.id), "formalite-guichet-unique/search");
  }

}
