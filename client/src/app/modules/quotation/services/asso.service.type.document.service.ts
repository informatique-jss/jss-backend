import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { AssoServiceDocument } from "../model/AssoServiceDocument";

@Injectable({
  providedIn: 'root'
})
export class AssoServiceTypeDocumentService extends AppRestService<AssoServiceDocument> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }
  addAssoServiceDocument(typeDocumentCode: string, serviceId: number) {
    return this.postItem(new HttpParams().set("typeDocumentCode", typeDocumentCode).set("serviceId", serviceId), "asso-service-document/add", null, "Type de document ajouté au service", "Erreur lors de l'ajout du type de document");
  }
}
