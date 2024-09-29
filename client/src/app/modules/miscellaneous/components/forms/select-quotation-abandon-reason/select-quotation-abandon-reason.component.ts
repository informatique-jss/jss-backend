import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { QuotationAbandonReason } from 'src/app/modules/miscellaneous/model/QuotationAbandonReason';
import { QuotationAbandonReasonService } from 'src/app/modules/miscellaneous/services/quotation.abandon.reason.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-quotation-abandon-reason',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})
export class SelectQuotationAbandonReasonComponent extends GenericSelectComponent<QuotationAbandonReason> implements OnInit {

  types: QuotationAbandonReason[] = [] as Array<QuotationAbandonReason>;

  constructor(private formBuild: UntypedFormBuilder, private quotationAbandonReasonService: QuotationAbandonReasonService, private appService3: AppService) {
    super(formBuild, appService3);
  }

  initTypes(): void {
    this.quotationAbandonReasonService.getQuotationAbandonReasons().subscribe(response => {
      this.types = response;
    });
  }
}
