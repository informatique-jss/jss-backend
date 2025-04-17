import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../services/appRest.service';
import { CommunicationPreference } from '../model/CommunicationPreference';

@Injectable({
  providedIn: 'root'
})
export class CommunicationPreferencesService extends AppRestService<CommunicationPreference> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  subscribeToNewspaperNewsletter(mail: string) {
    return this.get(new HttpParams().set("userMail", mail), "communication-preferences/subscribe-to-newspaper-newsletter", "Bienvenue ! Vous recevrez bientôt notre newsletter avec l’essentiel de l’actualité chaque semaine.", "Impossible de finaliser votre inscription. Vérifiez votre adresse e-mail et réessayez.");
  }
}
