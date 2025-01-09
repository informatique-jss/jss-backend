import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { FormaliteStatus } from '../../quotation/model/FormaliteStatus';

@Injectable({
  providedIn: 'root'
})
export class FormaliteStatusService extends AppRestService<FormaliteStatus> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getFormaliteStatus() {
    return this.getList(new HttpParams(), "formalite-status");
  }

  addOrUpdateFormaliteStatus(formaliteStatus: FormaliteStatus) {
    return this.addOrUpdate(new HttpParams(), "formalite-status", formaliteStatus, "Enregistré", "Erreur lors de l'enregistrement");
  }

  getFormaliteStatusByCode(status: FormaliteStatus[], code: string) {
    if (status)
      for (let statu of status)
        if (statu.code == code)
          return statu;
    return null;
  }
}
