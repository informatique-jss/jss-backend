import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { FormaliteInfogreffe } from '../../quotation/model/infogreffe/FormaliteInfogreffe';

@Injectable({
  providedIn: 'root'
})
export class FormaliteInfogreffeService extends AppRestService<FormaliteInfogreffe> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getFormaliteInfogreffeServiceByReference(value: string) {
    return this.getList(new HttpParams().set("value", value), "formalite-infogreffe/search");
  }

  addOrUpdateFormaliteInfogreffe(formaliteInfogreffe: FormaliteInfogreffe) {
    return this.addOrUpdate(new HttpParams(), "formalite-infogreffe/update", formaliteInfogreffe, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
