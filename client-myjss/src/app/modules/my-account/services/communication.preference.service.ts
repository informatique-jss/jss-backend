import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { CommunicationPreference } from '../model/CommunicationPreference';

@Injectable({
  providedIn: 'root'
})
export class CommunicationPreferencesService extends AppRestService<CommunicationPreference> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }


  getCommunicationPreferenceByMail(mail: string, validationToken: string | null) {
    return this.get(new HttpParams().set("userMail", mail).set("validationToken", validationToken ? validationToken : "null"), "communication-preferences/communication-preference");
  }

  subscribeToNewspaperNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-newspaper-newsletter");
  }

  unsubscribeToNewspaperNewsletter(mail: string, validationToken: string | null) {
    return this.get(new HttpParams().set("userMail", mail).set("validationToken", validationToken ? validationToken : "null"), "communication-preferences/unsubscribe-to-newspaper-newsletter");

  }

  subscribeToCorporateNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-corporate-newsletter");

  }

  unsubscribeToCorporateNewsletter(mail: string, validationToken: string | null) {
    return this.get(new HttpParams().set("userMail", mail).set("validationToken", validationToken ? validationToken : "null"), "communication-preferences/unsubscribe-to-corporate-newsletter");

  }


}
