import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { REPORTING_DATASET_QUOTATION, REPORTING_DATASET_QUOTATION_FOR_TIERS } from 'src/app/libs/Constants';
import { REPORTING_DATASET_VAT } from '../../../libs/Constants';
import { QuotationReportingService } from './quotation.reporting.service';
import { VatReportingService } from './vat.reporting.service';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  constructor(
    private quotationReportingService: QuotationReportingService,
    private vatReportingService: VatReportingService
  ) {
  }

  getDataset(dataset: string, filter: any | undefined): Observable<any> {
    return new Observable<any>(observer => {
      if (dataset == REPORTING_DATASET_QUOTATION) {
        this.quotationReportingService.getQuotationReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_QUOTATION_FOR_TIERS) {
        this.quotationReportingService.getQuotationReportingForTiers(filter).subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else if (dataset == REPORTING_DATASET_VAT) {
        this.vatReportingService.getVatReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else {
        observer.complete;
      }
    });
  }
}
