import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PaymentDeadlineType } from 'src/app/modules/tiers/model/PaymentDeadlineType';
import { PaymentDeadlineTypeService } from 'src/app/modules/tiers/services/payment.deadline.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-payment-deadline-type',
  templateUrl: './referential-payment-deadline-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialPaymentDeadlineTypeComponent extends GenericReferentialComponent<PaymentDeadlineType> implements OnInit {
  constructor(private paymentDeadlineTypeService: PaymentDeadlineTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<PaymentDeadlineType> {
    return this.paymentDeadlineTypeService.addOrUpdatePaymentDeadlineType(this.selectedEntity!);
  }
  getGetObservable(): Observable<PaymentDeadlineType[]> {
    return this.paymentDeadlineTypeService.getPaymentDeadlineTypes();
  }
}
