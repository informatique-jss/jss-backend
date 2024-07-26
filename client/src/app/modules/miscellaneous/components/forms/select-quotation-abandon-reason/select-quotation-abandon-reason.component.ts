import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { QuotationAbandonReason } from 'src/app/modules/miscellaneous/model/QuotationAbandonReason';
import { QuotationAbandonReasonService } from 'src/app/modules/miscellaneous/services/quotation.abandon.reason.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-quotation-abandon-reason',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})
export class SelectQuotationAbandonReasonComponent extends GenericSelectComponent<QuotationAbandonReason> implements OnInit {

  types: QuotationAbandonReason[] = [] as Array<QuotationAbandonReason>;

  constructor(private formBuild: UntypedFormBuilder, private quotationAbandonReasonService: QuotationAbandonReasonService) {
    super(formBuild);
  }

  initTypes(): void {
    this.quotationAbandonReasonService.getQuotationAbandonReasons().subscribe(response => {
      this.types = response;
    });
  }
}
