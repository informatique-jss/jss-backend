import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { Mail } from '../../tiers/model/Mail';

@Injectable({
  providedIn: 'root'
})
export class MailService extends AppRestService<Mail>{

  constructor(http: HttpClient) {
    super(http, "tiers");
  }

  getMails(mail: string) {
    return this.getList(new HttpParams().set("mail", mail), "mails/search");
  }

}
