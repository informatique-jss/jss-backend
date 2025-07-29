import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { AppRestService } from "src/app/services/appRest.service";
import { Affaire } from "../model/Affaire";
import { BeneficialOwner } from '../model/beneficial-owner/BeneficialOwner';

@Injectable({
  providedIn: 'root'
})
export class BeneficialOwnerService extends AppRestService<BeneficialOwner> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  addOrUpdateBeneficialOwner(beneficialOwner: BeneficialOwner) {
    return this.postItem(new HttpParams(), "beneficial-owner", beneficialOwner);
  }
  getBeneficialOwners(affaire: Affaire) {
    return this.getList(new HttpParams().set("idAffaire", affaire.id), "beneficial-owners");
  }
  deleteBeneficialOwner(beneficialOwner: BeneficialOwner) {
    return this.get(new HttpParams().set("beneficialOwnerId", beneficialOwner.id), "beneficial-owner/delete");
  }
}
