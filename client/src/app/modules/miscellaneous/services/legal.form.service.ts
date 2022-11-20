import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { LegalForm } from '../../miscellaneous/model/LegalForm';

@Injectable({
  providedIn: 'root'
})
export class LegalFormService extends AppRestService<LegalForm>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getLegalForms() {
    return this.getListCached(new HttpParams(), "legal-forms");
  }

  addOrUpdateLegalForm(legalForm: LegalForm) {
    this.clearListCache(new HttpParams(), "legal-forms");
    return this.addOrUpdate(new HttpParams(), "legal-form", legalForm, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
