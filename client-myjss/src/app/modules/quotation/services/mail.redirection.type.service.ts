import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
import { MailRedirectionType } from '../model/MailRedirectionType';

@Injectable({
  providedIn: 'root'
})
export class MailRedirectionTypeService extends AppRestService<MailRedirectionType> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getMailRedirectionTypes() {
    return this.getListCached(new HttpParams(), "mail-redirection-types");
  }
}
