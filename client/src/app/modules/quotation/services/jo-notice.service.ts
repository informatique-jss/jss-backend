import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { JoNotice } from '../model/JoNotice';

@Injectable({
  providedIn: 'root'
})
export class JoNoticeService extends AppRestService<JoNotice> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getJoNoticeForAffaire(affaireId: number) {
    return this.getList(new HttpParams().set("affaireId", affaireId), "jo-notice/affaire");
  }
}
