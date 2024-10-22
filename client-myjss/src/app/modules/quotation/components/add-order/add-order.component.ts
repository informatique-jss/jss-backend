import { Component, OnInit } from '@angular/core';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { TYPE_CHOSEN_ORDER, TYPE_CHOSEN_QUOTATION } from '../choose-type/choose-type.component';

@Component({
  selector: 'app-add-order',
  templateUrl: './add-order.component.html',
  styleUrls: ['./add-order.component.css']
})
export class AddOrderComponent implements OnInit {
  typeChosen: string | undefined;
  servicesChosen: ServiceType[] = [];
  displayAddNewService: boolean = true;

  constructor() { }

  ngOnInit() {
  }

  chooseType(typeChosen: string) {
    this.typeChosen = typeChosen;
  }

  isAnOrder() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_ORDER;
  }

  isAQuotation() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_QUOTATION;
  }

  chooseNewService(service: ServiceType) {
    if (service) {
      this.servicesChosen.push(service);
      this.displayAddNewService = false;
    }
  }

}
