import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { LegalForm } from '../model/LegalForm';

@Injectable({
  providedIn: 'root'
})
export class LegalFormService extends AppRestService<LegalForm> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getLegalFormsFilteredByName(label: string, page: number, pageSize: number) {
    let params = new HttpParams();
    params = params.set("label", label);
    params = params.set("page", page);
    params = params.set("size", pageSize);
    return this.getPagedList(params, "legal-forms/label");
  }
}
