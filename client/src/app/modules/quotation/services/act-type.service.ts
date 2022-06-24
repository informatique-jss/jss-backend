import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { ActType } from '../../quotation/model/ActType';

@Injectable({
  providedIn: 'root'
})
export class ActTypeService extends AppRestService<ActType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getActTypes() {
    return this.getList(new HttpParams(), "act-types");
  }

  addOrUpdateActType(actType: ActType) {
    return this.addOrUpdate(new HttpParams(), "act-type", actType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
