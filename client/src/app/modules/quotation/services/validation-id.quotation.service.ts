import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class ValidationIdQuotationService extends AppRestService<number> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getValidationIdForQuotation() {
    return this.get(new HttpParams(), "quotation/validation-id");
  }

}
