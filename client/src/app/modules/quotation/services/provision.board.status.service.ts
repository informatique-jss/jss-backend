import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AppRestService } from 'src/app/services/appRest.service';
import { IProvisionStatus } from '../../miscellaneous/model/IProvisionStatus';
import { IWorkflowElement } from '../../miscellaneous/model/IWorkflowElement';
import { QuotationSearch } from '../model/QuotationSearch';
import { QuotationSearchResult } from '../model/QuotationSearchResult';

@Injectable({
  providedIn: 'root'
})
export class ProvisionBoardStatusService extends AppRestService<IProvisionStatus>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getBoardAnnouncementStatus() {
    return this.postListList(new HttpParams(), "dashboard/announcement-status");
  }

}
