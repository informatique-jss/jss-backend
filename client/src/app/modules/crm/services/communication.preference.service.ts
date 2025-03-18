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
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-newspaper-newsletter", "Le responsable est abonné à la newsletter du journal", "Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.");
  }

  unsubscribeToNewspaperNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/unsubscribe-to-newspaper-newsletter", "Le responsable est désabonné à la newsletter du journal JSS", "Une erreur est survenues, vous êtes toujours inscrit à la newsletter, merci de réessayer plus tard");
  }

  subscribeToCorporateNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-corporate-newsletter", "Le responsable est abonné à la newsletter MyJSS", "Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.");
  }

  unsubscribeToCorporateNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/unsubscribe-to-corporate-newsletter", "Le responsable est désabonné à la newsletter de MyJSS", "Une erreur est survenues, vous êtes toujours inscrit à la newsletter, merci de réessayer plus tard");
  }
}
