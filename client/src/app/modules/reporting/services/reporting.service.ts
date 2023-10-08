import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { REPORTING_DATASET_ANNOUNCEMENT, REPORTING_DATASET_CUSTOMER_ORDER, REPORTING_DATASET_PROVISION, REPORTING_DATASET_QUOTATION, REPORTING_DATASET_TIERS, REPORTING_DATASET_TURNOVER_AMOUNT } from 'src/app/libs/Constants';
import { AnnouncementReportingService } from './announcement.reporting.service';
import { CustomerOrderReportingService } from './customer.order.reporting.service';
import { ProvisionReportingService } from './provision.reporting.service';
import { QuotationReportingService } from './quotation.reporting.service';
import { TiersReportingService } from './tiers.reporting.service';
import { TurnoverReportingService } from './turnover.reporting.service';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  constructor(
    private quotationReportingService: QuotationReportingService,
    private turnoverReportingService: TurnoverReportingService,
    private customerOrderReportingService: CustomerOrderReportingService,
    private provisionReportingService: ProvisionReportingService,
    private announcementReportingService: AnnouncementReportingService,
    private tiersReportingService: TiersReportingService,
  ) {
  }

  getDataset(dataset: string, filter: any | undefined): Observable<any> {
    return new Observable<any>(observer => {
      if (dataset == REPORTING_DATASET_QUOTATION) {
        this.quotationReportingService.getQuotationReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_TURNOVER_AMOUNT) {
        this.turnoverReportingService.getTurnoverReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_CUSTOMER_ORDER) {
        this.customerOrderReportingService.getCustomerOrderReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_PROVISION) {
        this.provisionReportingService.getProvisionReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_ANNOUNCEMENT) {
        this.announcementReportingService.getAnnouncementReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_TIERS) {
        this.tiersReportingService.getTiersReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else {
        observer.complete;
      }
    });
  }
}
