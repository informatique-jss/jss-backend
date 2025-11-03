import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { OrderBlockage } from 'src/app/modules/quotation/model/OrderBlockage';
import { OrderBlockageService } from 'src/app/modules/quotation/services/order.blockage.service';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-order-blockage',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css'],
  standalone: false
})
export class SelectOrderBlockageComponent extends GenericSelectComponent<OrderBlockage> implements OnInit {

  @Input() types: OrderBlockage[] = [] as Array<OrderBlockage>;

  constructor(private formBuild: UntypedFormBuilder,
    private orderBlockageService: OrderBlockageService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.orderBlockageService.getOrderBlockages().subscribe(response => {
      this.types = response;
    })
  }
}
