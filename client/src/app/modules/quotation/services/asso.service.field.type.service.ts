import { AppRestService } from "src/app/services/appRest.service";
import { AssoServiceFieldType } from "../model/AssoServiceFieldType";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class AssoServiceFieldTypeService extends AppRestService<AssoServiceFieldType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }
  addAssoServiceFieldType(serviceId: number, serviceFieldTypeId: number) {
    return this.postItem(new HttpParams().set("serviceId", serviceId).set("serviceFieldTypeId", serviceFieldTypeId), "asso-service-field-type/add", null, "Type de champ ajouté au service", "Erreur lors de l'ajout du type de champ");
  }
}
