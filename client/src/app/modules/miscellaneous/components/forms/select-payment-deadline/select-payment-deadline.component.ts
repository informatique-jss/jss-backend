import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { PaymentDeadlineType } from 'src/app/modules/tiers/model/PaymentDeadlineType';
import { PaymentDeadlineTypeService } from 'src/app/modules/tiers/services/payment.deadline.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-payment-deadline',
  templateUrl: './select-payment-deadline.component.html',
  styleUrls: ['./select-payment-deadline.component.css']
})
export class SelectPaymentDeadlineComponent extends GenericSelectComponent<PaymentDeadlineType> implements OnInit {

  types: PaymentDeadlineType[] = [] as Array<PaymentDeadlineType>;

  constructor(private formBuild: UntypedFormBuilder, private paymentDeadlineTypeService: PaymentDeadlineTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.paymentDeadlineTypeService.getPaymentDeadlineTypes().subscribe(response => {
      this.types = response;
    })
  }
}
