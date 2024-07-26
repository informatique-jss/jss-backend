import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PaymentSearchResultService } from 'src/app/modules/invoicing/services/payment.search.result.service';
import { PaymentSearch } from '../../../../invoicing/model/PaymentSearch';
import { PaymentSearchResult } from '../../../../invoicing/model/PaymentSearchResult';
import { ConstantService } from '../../../services/constant.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-payment',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompletePaymentComponent extends GenericAutocompleteComponent<PaymentSearchResult, PaymentSearchResult> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private paymentSearchResultService: PaymentSearchResultService,
    private constantService: ConstantService,) {
    super(formBuild)
  }

  @Input() forcedMinAmount = undefined;
  @Input() forcedMaxAmount = 0;
  @Input() hideAssociatedPayment = true;

  searchEntities(value: string): Observable<PaymentSearchResult[]> {
    let search: PaymentSearch = {} as PaymentSearch;
    search.label = value;
    if (this.forcedMinAmount)
      search.minAmount = this.forcedMinAmount;
    if (this.forcedMaxAmount)
      search.maxAmount = this.forcedMaxAmount;
    search.isHideAssociatedPayments = this.hideAssociatedPayment;
    return this.paymentSearchResultService.getPayments(search);
  }

  displayLabel(payment: PaymentSearchResult): string {
    if (payment)
      return payment.id + " (" + payment.paymentAmount + " €)";
    return "";
  }
}
