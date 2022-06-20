import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { PaymentDeadlineType } from 'src/app/modules/tiers/model/PaymentDeadlineType';
import { PaymentDeadlineTypeService } from 'src/app/modules/tiers/services/payment.deadline.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-payment-deadline',
  templateUrl: './select-payment-deadline.component.html',
  styleUrls: ['./select-payment-deadline.component.css']
})
export class SelectPaymentDeadlineComponent extends GenericSelectComponent<PaymentDeadlineType> implements OnInit {

  types: PaymentDeadlineType[] = [] as Array<PaymentDeadlineType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: FormBuilder, private paymentDeadlineTypeService: PaymentDeadlineTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.paymentDeadlineTypeService.getPaymentDeadlineTypes().subscribe(response => {
      this.types = response;
    })
  }
}
