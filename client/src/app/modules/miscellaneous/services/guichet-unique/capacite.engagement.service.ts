import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CapaciteEngagement } from 'src/app/modules/quotation/model/guichet-unique/referentials/CapaciteEngagement';
import { AppRestService } from 'src/app/services/appRest.service';

@Injectable({
  providedIn: 'root'
})
export class CapaciteEngagementService extends AppRestService<CapaciteEngagement>{

  constructor(http: HttpClient) {
    super(http, 'quotation/guichet-unique');
  }

  getCapaciteEngagement() {
    return this.getList(new HttpParams(), 'capacite-engagement');
  }

}                        
