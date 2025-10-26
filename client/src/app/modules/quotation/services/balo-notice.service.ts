import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { BaloNotice } from '../model/BaloNotice';

@Injectable({
  providedIn: 'root'
})
export class BaloNoticeService extends AppRestService<BaloNotice> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBaloNoticeForAffaire(affaireId: number) {
    return this.getList(new HttpParams().set("affaireId", affaireId), "balo-notice/affaire");
  }
}
