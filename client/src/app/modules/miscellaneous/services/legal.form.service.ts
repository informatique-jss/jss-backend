import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { LegalForm } from '../../miscellaneous/model/LegalForm';

@Injectable({
  providedIn: 'root'
})
export class LegalFormService extends AppRestService<LegalForm>{

  constructor(http: HttpClient) {
    super(http, "miscellaneous");
  }

  getLegalForms() {
    return this.getList(new HttpParams(), "legal-forms");
  }

}
