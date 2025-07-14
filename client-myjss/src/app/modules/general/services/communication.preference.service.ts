import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../main/services/appRest.service';
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
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-newspaper-newsletter", "Bienvenue ! Vous recevrez bientôt notre newsletter avec l’essentiel de l’actualité chaque semaine.", "Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.");
  }

  unsubscribeToNewspaperNewsletter(mail: string, validationToken: string | null) {
    return this.get(new HttpParams().set("userMail", mail).set("validationToken", validationToken ? validationToken : "null"), "communication-preferences/unsubscribe-to-newspaper-newsletter", "Vous êtes bien désinscrit à la newsletter du journal JSS", "Une erreur est survenues, vous êtes toujours inscrit à la newsletter, merci de réessayer plus tard");
  }

  subscribeToCorporateNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-corporate-newsletter", "Bienvenue ! Vous recevrez bientôt notre newsletter avec l’essentiel de l’actualité chaque semaine.", "Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.");
  }

  unsubscribeToCorporateNewsletter(mail: string, validationToken: string | null) {
    return this.get(new HttpParams().set("userMail", mail).set("validationToken", validationToken ? validationToken : "null"), "communication-preferences/unsubscribe-to-corporate-newsletter", "Vous êtes bien désinscrit à la newsletter de MyJSS", "Une erreur est survenues, vous êtes toujours inscrit à la newsletter, merci de réessayer plus tard");
  }
}
