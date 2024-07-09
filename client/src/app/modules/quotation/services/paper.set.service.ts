import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { PaperSet } from '../../quotation/model/PaperSet';

@Injectable({
  providedIn: 'root'
})
export class PaperSetService extends AppRestService<PaperSet> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getPaperSets() {
    return this.getList(new HttpParams(), "paper-sets");
  }

  cancelPaperSet(paperSetId: number) {
    return this.get(new HttpParams().set("paperSetId", paperSetId), "paper-set/cancel");
  }

  addOrUpdatePaperSet(paperSet: PaperSet) {
    return this.addOrUpdate(new HttpParams(), "paper-set", paperSet, "Enregistré", "Erreur lors de l'enregistrement");
  }

  validatePaperSet(paperSetId: number) {
    return this.get(new HttpParams().set("paperSetId", paperSetId), "paper-set/validate");
  }

}
