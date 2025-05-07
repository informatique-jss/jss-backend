import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { WebinarParticipant } from '../model/WebinarParticipant';

@Injectable({
  providedIn: 'root'
})
export class WebinarParticipantService extends AppRestService<WebinarParticipant> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  subscribeWebinar(webinarParticipant: WebinarParticipant) {
    return this.postItem(new HttpParams(), 'webinar/subscribe', webinarParticipant);
  }
}
