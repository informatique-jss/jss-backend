import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { MailRedirectionType } from '../../quotation/model/MailRedirectionType';

@Injectable({
  providedIn: 'root'
})
export class MailRedirectionTypeService extends AppRestService<MailRedirectionType>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getMailRedirectionTypes() {
    return this.getListCached(new HttpParams(), "mail-redirection-types");
  }

  addOrUpdateMailRedirectionType(mailRedirectionType: MailRedirectionType) {
    this.clearListCache(new HttpParams(), "mail-redirection-types");
    return this.addOrUpdate(new HttpParams(), "mail-redirection-type", mailRedirectionType, "Enregistré", "Erreur lors de l'enregistrement");
  }
}
