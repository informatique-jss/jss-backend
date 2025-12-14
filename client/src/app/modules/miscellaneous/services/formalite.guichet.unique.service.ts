import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { FormaliteGuichetUnique } from '../../quotation/model/guichet-unique/FormaliteGuichetUnique';

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

}
