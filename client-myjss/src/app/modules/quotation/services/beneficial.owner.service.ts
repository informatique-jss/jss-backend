import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "../../main/services/appRest.service";
import { Provision } from "../../my-account/model/Provision";
import { BeneficialOwner } from "../model/BeneficialOwner";

@Injectable({
  providedIn: 'root'
})
export class BeneficialOwnerService extends AppRestService<BeneficialOwner> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  addOrUpdateBeneficialOwner(beneficialOwner: BeneficialOwner, provision: Provision) {
    return this.postItem(new HttpParams().set("idProvision", provision.id), "beneficial-owner-provision", beneficialOwner);
  }

  getBeneficialOwnersByProvision(provision: Provision) {
    return this.getList(new HttpParams().set("idProvision", provision.id), "beneficial-owners-provision");
  }

  deleteBeneficialOwner(beneficialOwner: BeneficialOwner) {
    return this.get(new HttpParams().set("beneficialOwnerId", beneficialOwner.id), "beneficial-owner/delete");
  }
}
