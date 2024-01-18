import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { QuotationAbandonReason } from 'src/app/modules/miscellaneous/model/QuotationAbandonReason';
import { QuotationAbandonReasonService } from 'src/app/modules/miscellaneous/services/quotation.abandon.reason.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-quotation-abandon-reason',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialQuotationAbandonReasonComponent extends GenericReferentialComponent<QuotationAbandonReason> implements OnInit {
  constructor(private quotationAbandonReasonService: QuotationAbandonReasonService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }
  getAddOrUpdateObservable(): Observable<QuotationAbandonReason> {
    return this.quotationAbandonReasonService.addOrUpdateQuotationAbandonReason(this.selectedEntity!);
  }

  getGetObservable(): Observable<QuotationAbandonReason[]> {
    return this.quotationAbandonReasonService.getQuotationAbandonReasons();
  }
}
