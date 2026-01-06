import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { PaymentDto } from '../model/PaymentDto';
import { PaymentSearch } from '../model/PaymentSearch';

@Injectable({
  providedIn: 'root'
})
export class PaymentService extends AppRestService<PaymentDto> {

  constructor(http: HttpClient) {
    super(http, "invoicing");
  }

  selectedPayment: PaymentDto[] = [];
  selectedPaymentUnique: PaymentDto | undefined;
  selectedPaymentUniqueChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  getCurrentSelectedPayment() {
    if (this.selectedPayment.length == 0) {
      let toParse = localStorage.getItem("selected-payment");
      if (toParse)
        this.selectedPayment = JSON.parse(toParse);
    }
    return this.selectedPayment;
  }

  setCurrentSelectedPayment(payments: PaymentDto[]) {
    this.selectedPayment = payments;
    localStorage.setItem("selected-payment", JSON.stringify(payments));
  }

  getSelectedPaymentUnique() {
    return this.selectedPaymentUnique;
  }

  getSelectedPaymentUniqueChangeEvent() {
    return this.selectedPaymentUniqueChange.asObservable();
  }

  setSelectedPaymentUnique(PaymentDto: PaymentDto) {
    this.selectedPaymentUnique = PaymentDto;
    this.selectedPaymentUniqueChange.next(true);
  }

  searchPayment(paymentSearch: PaymentSearch) {
    return this.postList(new HttpParams(), "payments/search/v2", paymentSearch);
  }
}
