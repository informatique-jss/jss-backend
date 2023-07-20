import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { REPORTING_DATASET_CUSTOMER_ORDER, REPORTING_DATASET_CUSTOMER_ORDER_FOR_TIERS, REPORTING_DATASET_QUOTATION, REPORTING_DATASET_TIERS } from 'src/app/libs/Constants';
import { REPORTING_DATASET_VAT } from '../../../libs/Constants';
import { CustomerOrderReportingService } from './customer.order.reporting.service';
import { QuotationReportingService } from './quotation.reporting.service';
import { TiersReportingService } from './tiers.reporting.service';
import { VatReportingService } from './vat.reporting.service';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  constructor(
    private customerOrderReportingService: CustomerOrderReportingService,
    private vatReportingService: VatReportingService,
    private tiersReportingService: TiersReportingService,
    private quotationReportingService: QuotationReportingService,
  ) {
  }

  getDataset(dataset: string, filter: any | undefined): Observable<any> {
    return new Observable<any>(observer => {
      if (dataset == REPORTING_DATASET_CUSTOMER_ORDER) {
        this.customerOrderReportingService.getCustomerOrderReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_CUSTOMER_ORDER_FOR_TIERS) {
        this.customerOrderReportingService.getCustomerOrderReportingForTiers(filter).subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_VAT) {
        this.vatReportingService.getVatReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_QUOTATION) {
        this.quotationReportingService.getQuotationReporting().subscribe(data => {
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
