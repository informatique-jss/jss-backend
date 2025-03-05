import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { Mail } from '../../profile/model/Mail';

@Injectable({
  providedIn: 'root'
})
export class NewsletterService extends AppRestService<Mail> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  saveEmailForNewsletter(email: string) {
    return this.postItem(new HttpParams().set("customerEmail", email), "newsletter/add", "Bienvenue ! Vous recevrez bientôt notre newsletter avec l\’essentiel de l\’actualité chaque semaine.", "Inscription à la newsletter impossible, merci de réessayer plus tard.");
  }

}
