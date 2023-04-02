import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';
import { Payment } from '../../model/Payment';
import { PaymentService } from '../../services/payment.service';

@Component({
  selector: 'add-payment',
  templateUrl: './add-payment.component.html',
  styleUrls: ['./add-payment.component.css']
})
export class AddPaymentComponent implements OnInit {

  newPayment: Payment = {} as Payment;

  constructor(
    private appService: AppService,
    private formBuilder: FormBuilder,
    private paymentService: PaymentService,
    private constantService: ConstantService,
  ) { }

  addPaymentForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Ajouter un paiement par chèque")
  }

  addPayment() {
    if (this.newPayment && this.addPaymentForm.valid) {
      this.newPayment.paymentWay = this.constantService.getPaymentWayInbound();
      this.newPayment.paymentType = this.constantService.getPaymentTypeCheques();
      this.newPayment.isCancelled = false;
      this.newPayment.isExternallyAssociated = false;
      this.paymentService.addCheckPayment(this.newPayment).subscribe(response => {
        this.appService.openRoute(null, "/invoicing", undefined);
      })
    }
  }
}
