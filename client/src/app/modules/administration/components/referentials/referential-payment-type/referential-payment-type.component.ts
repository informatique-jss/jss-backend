import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PaymentType } from 'src/app/modules/miscellaneous/model/PaymentType';
import { PaymentTypeService } from 'src/app/modules/miscellaneous/services/payment.type.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-payment-type',
  templateUrl: './referential-payment-type.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialPaymentTypeComponent extends GenericReferentialComponent<PaymentType> implements OnInit {
  constructor(private paymentTypeService: PaymentTypeService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<PaymentType> {
    return this.paymentTypeService.addOrUpdatePaymentType(this.selectedEntity!);
  }
  getGetObservable(): Observable<PaymentType[]> {
    return this.paymentTypeService.getPaymentTypes();
  }
}
