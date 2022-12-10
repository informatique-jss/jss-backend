import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { IQuotation } from '../model/IQuotation';
import { MailComputeResult } from '../model/MailComputeResult';

@Injectable({
  providedIn: 'root'
})
export class MailComputeResultService extends AppRestService<MailComputeResult>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getMailComputeResultForBilling(quotation: IQuotation) {
    return this.postItem(new HttpParams(), "mail/billing/compute", quotation);
  }

}
