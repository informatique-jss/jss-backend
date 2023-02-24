import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { REPORTING_DATASET_QUOTATION } from 'src/app/libs/Constants';
import { QuotationReportingService } from './quotation.reporting.service';

@Injectable({
  providedIn: 'root'
})
export class ReportingService {

  constructor(
    private quotationReportingService: QuotationReportingService,
  ) {
  }

  getDataset(dataset: string): Observable<any> {
    return new Observable<any>(observer => {
      if (dataset == REPORTING_DATASET_QUOTATION) {
        this.quotationReportingService.getQuotationReporting().subscribe(data => {
          observer.next(data);
          observer.complete;
        });
      } else {
        observer.complete;
      }
    });
  }
}
