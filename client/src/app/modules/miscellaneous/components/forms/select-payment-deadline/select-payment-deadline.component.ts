import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PaymentDeadlineType } from 'src/app/modules/tiers/model/PaymentDeadlineType';
import { PaymentDeadlineTypeService } from 'src/app/modules/tiers/services/payment.deadline.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-payment-deadline',
  templateUrl: './select-payment-deadline.component.html',
  styleUrls: ['./select-payment-deadline.component.css']
})
export class SelectPaymentDeadlineComponent extends GenericSelectComponent<PaymentDeadlineType> implements OnInit {

  types: PaymentDeadlineType[] = [] as Array<PaymentDeadlineType>;

  constructor(private formBuild: UntypedFormBuilder, private paymentDeadlineTypeService: PaymentDeadlineTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.paymentDeadlineTypeService.getPaymentDeadlineTypes().subscribe(response => {
      this.types = response;
    })
  }
}
