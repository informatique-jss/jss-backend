import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../services/appRest.service';
import { Webinar } from '../model/Webinar';
import { WebinarParticipant } from '../model/WebinarParticipant';

@Injectable({
  providedIn: 'root'
})
export class WebinarParticipantService extends AppRestService<WebinarParticipant> {

  constructor(http: HttpClient) {
    super(http, "crm");
  }

  getWebinarParticipants(webinar: Webinar) {
    return this.getList(new HttpParams().set("webinarId", webinar.id), "webinar-participants");
  }

  deleteWebinarParticipant(webinarParticipant: WebinarParticipant) {
    return this.get(new HttpParams().set("webinarParticipantId", webinarParticipant.id), "webinar-participant/delete");
  }
}
