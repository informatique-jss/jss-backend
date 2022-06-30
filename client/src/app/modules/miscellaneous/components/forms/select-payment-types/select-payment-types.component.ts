import { ChangeDetectorRef, Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
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
      if (!this.filteredPaymentTypeCodes && !this.filteredPaymentType) {
        this.types = response;
      } else {
        if (this.filteredPaymentTypeCodes) {
          for (let paymentType of response) {
            for (let filter of this.filteredPaymentTypeCodes)
              if (filter == paymentType.code)
                this.types.push(paymentType);
          }
        } else if (this.filteredPaymentType) {
          for (let paymentType of response) {
            for (let filter of this.filteredPaymentType)
              if (filter.code == paymentType.code)
                this.types.push(paymentType);
          }
        }
      }
    })
  }
}
