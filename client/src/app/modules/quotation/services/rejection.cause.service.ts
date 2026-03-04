import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { RejectionCause } from '../../quotation/model/RejectionCause';

@Injectable({
  providedIn: 'root'
})
export class RejectionCauseService extends AppRestService<RejectionCause>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getRejectionCauses() {
    return this.getList(new HttpParams(), "rejection-causes");
  }
  
   addOrUpdateRejectionCause(rejectionCause: RejectionCause) {
    return this.addOrUpdate(new HttpParams(), "rejection-cause", rejectionCause, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
