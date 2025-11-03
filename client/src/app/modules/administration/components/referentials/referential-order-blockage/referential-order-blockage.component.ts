import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { OrderBlockage } from 'src/app/modules/quotation/model/OrderBlockage';
import { OrderBlockageService } from 'src/app/modules/quotation/services/order.blockage.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-order-blockage',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialOrderBlockageComponent extends GenericReferentialComponent<OrderBlockage> implements OnInit {
  constructor(private orderBlockageService: OrderBlockageService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<OrderBlockage> {
    return this.orderBlockageService.addOrUpdateOrderBlockage(this.selectedEntity!);
  }
  getGetObservable(): Observable<OrderBlockage[]> {
    return this.orderBlockageService.getOrderBlockages();
  }
}
