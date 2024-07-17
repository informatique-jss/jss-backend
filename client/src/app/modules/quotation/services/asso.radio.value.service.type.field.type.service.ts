import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaperSet } from '../../quotation/model/PaperSet';
import { AssoRadioValueServiceTypeFieldType } from '../model/AssoRadioValueServiceTypeFieldType';

@Injectable({
  providedIn: 'root'
})
export class AssoRadioValueServiceTypeFieldTypeService extends AppRestService<AssoRadioValueServiceTypeFieldType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getAssoRadioValueServiceTypeFieldTypes() {
    return this.getList(new HttpParams(), "asso-radio-value");
  }

  cancelAssoRadioValueServiceTypeFieldType(assoRadioValueId: number) {
    return this.get(new HttpParams().set("assoRadioValueId", assoRadioValueId), "asso-radio-value/cancel");
  }

  addOrUpdateAssoRadioValueServiceTypeFieldType(assoRadioValueServiceTypeFieldType: AssoRadioValueServiceTypeFieldType) {
    return this.addOrUpdate(new HttpParams(), "", assoRadioValueServiceTypeFieldType, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
