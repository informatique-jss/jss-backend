import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { FormaliteGuichetUnique } from '../../quotation/model/guichet-unique/FormaliteGuichetUnique';
import { RegularizationRequest } from '../../quotation/model/guichet-unique/RegularizationRequest';
import { RejectionCause } from '../../quotation/model/RejectionCause';

@Injectable({
  providedIn: 'root'
})
export class FormaliteGuichetUniqueService extends AppRestService<FormaliteGuichetUnique> {
  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  cloneLiasse(liasseNumber: string) {
    return this.get(new HttpParams().set("liasseNumber", liasseNumber), "formalite-guichet-unique/clone", "La liasse clonée est disponible dans vos brouillons. Attention à bien revérifier que toutes les options nécessaires ont été cochées.");
  }

  getFormaliteGuichetUniqueServiceByReference(value: string) {
    return this.getList(new HttpParams().set("value", value), "formalite-guichet-unique/search");
  }

  getFormaliteGuichetUniqueService(id: number) {
    return this.get(new HttpParams().set("idFormaliteGuichetUnique", id), "formalite-guichet-unique");
  }

  changeRejectionCause(formaliteGuichetUnique: FormaliteGuichetUnique, regularizationRequest: RegularizationRequest, rejectionCause: RejectionCause) {
    return this.get(new HttpParams().set("idFormaliteGuichetUnique", formaliteGuichetUnique.id).set("idRegularizationRequest", regularizationRequest.id).set("idRejectionCause", rejectionCause.id), "formalite-guichet-unique/rejection/cause");
  }

}
