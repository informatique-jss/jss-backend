import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PAYMENT_TYPE_PRELEVEMENT } from 'src/app/libs/Constants';
import { PaymentType } from '../../../model/PaymentType';
import { PaymentTypeService } from '../../../services/payment.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-payment-types',
  templateUrl: './select-payment-types.component.html',
  styleUrls: ['./select-payment-types.component.css']
})
export class SelectPaymentTypesComponent extends GenericSelectComponent<PaymentType> implements OnInit {

  types: PaymentType[] = [] as Array<PaymentType>;

  /**
 * Limit the list of payment type code displayed
 */
  @Input() filteredPaymentTypeCodes: string[] | undefined;
  /**
 * Limit the list of payment type code displayed
 */
  @Input() filteredPaymentType: PaymentType[] | undefined;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder, private paymentTypeService: PaymentTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.paymentTypeService.getPaymentTypes().subscribe(response => {
      this.types = [];
      if (!this.filteredPaymentTypeCodes && !this.filteredPaymentType) {
        this.types = response;
      } else {
        if (this.filteredPaymentTypeCodes) {
          for (let paymentType of response) {
            for (let filter of this.filteredPaymentTypeCodes)
              if (filter == paymentType.code && this.types.indexOf(paymentType) < 0)
                this.types.push(paymentType);
          }
        } else if (this.filteredPaymentType) {
          for (let paymentType of response) {
            for (let filter of this.filteredPaymentType)
              if (filter.code == paymentType.code && this.types.indexOf(paymentType) < 0)
                this.types.push(paymentType);
          }
        }
      }
      if ((this.model == null || this.model == undefined) && this.types.length > 0) {
        for (const paymentType of this.types) {
          if (paymentType.code == PAYMENT_TYPE_PRELEVEMENT)
            this.model = paymentType;
          this.modelChange.emit(this.model);
        }
      }
    })
  }
}
