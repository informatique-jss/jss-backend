import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaperSetType } from '../../miscellaneous/model/PaperSetType';

@Injectable({
  providedIn: 'root'
})
export class PaperSetTypeService extends AppRestService<PaperSetType> {

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getPaperSetTypes() {
    return this.getList(new HttpParams(), "paper-set-types");
  }

  addOrUpdatePaperSetType(paperSetType: PaperSetType) {
    return this.addOrUpdate(new HttpParams(), "paper-set-type", paperSetType, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
