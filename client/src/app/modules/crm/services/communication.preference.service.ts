import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CommunicationPreference } from '../model/CommunicationPreference';

@Injectable({
  providedIn: 'root'
})
export class CommunicationPreferenceService extends AppRestService<CommunicationPreference> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  getCommunicationPreferenceByMail(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/communication-preference");
  }

  subscribeToNewspaperNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-newspaper-newsletter");
  }

  unsubscribeToNewspaperNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/unsubscribe-to-newspaper-newsletter");

  }

  subscribeToCorporateNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-corporate-newsletter");

  }

  unsubscribeToCorporateNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/unsubscribe-to-corporate-newsletter");

  }
}
