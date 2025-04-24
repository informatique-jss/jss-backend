import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../../libs/appRest.service";
import { ServiceType } from "../../my-account/model/ServiceType";
import { TypeDocument } from "../../my-account/model/TypeDocument";

@Injectable({
  providedIn: 'root'
})
export class TypeDocumentService extends AppRestService<TypeDocument> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }
  getTypeDocumentMandatoryByServiceType(serviceType: ServiceType) {
    return this.getListCached(new HttpParams().set("serviceTypeId", serviceType.id), "type-documents/service-type");
  }
}
