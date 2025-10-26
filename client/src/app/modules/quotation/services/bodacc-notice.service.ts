import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BodaccNotice } from '../model/BodaccNotice';

@Injectable({
  providedIn: 'root'
})
export class BodaccNoticeService extends AppRestService<BodaccNotice> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBodaccNoticeForAffaire(affaireId: number) {
    return this.getList(new HttpParams().set("affaireId", affaireId), "bodacc-notice/affaire");
  }
}
