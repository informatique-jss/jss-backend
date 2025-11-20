import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { ServiceFieldType } from '../../my-account/model/ServiceFieldType';

@Injectable({
  providedIn: 'root'
})
export class ServiceFieldTypeService extends AppRestService<ServiceFieldType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  // Get all ServiceFieldType. If affaireId is defined and exists, then the values of the 
  // ServiceFieldType will be filled with the values found in the RNE
  getServiceFieldTypes(affaireId: number | undefined) {
    let params = new HttpParams()
    if (affaireId)
      params = params.set("affaireId", affaireId);
    else
      params = params.set("affaireId", "");
    return this.getList(params, "service-field-types");
  }
}
