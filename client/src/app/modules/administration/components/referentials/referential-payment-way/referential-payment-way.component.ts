import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { PaymentWay } from 'src/app/modules/invoicing/model/PaymentWay';
import { PaymentWayService } from 'src/app/modules/invoicing/services/payment.way.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-payment-way',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialPaymentWayComponent extends GenericReferentialComponent<PaymentWay> implements OnInit {
  constructor(private paymentWayService: PaymentWayService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<PaymentWay> {
    return this.paymentWayService.addOrUpdatePaymentWay(this.selectedEntity!);
  }
  getGetObservable(): Observable<PaymentWay[]> {
    return this.paymentWayService.getPaymentWays();
  }
}
