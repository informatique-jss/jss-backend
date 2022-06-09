import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/appRest.service';
import { MailRedirectionType } from '../../quotation/model/MailRedirectionType';

@Injectable({
  providedIn: 'root'
})
export class MailRedirectionTypeService extends AppRestService<MailRedirectionType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getMailRedirectionTypes() {
    return this.getList(new HttpParams(), "mail-redirection-types");
  }

}
