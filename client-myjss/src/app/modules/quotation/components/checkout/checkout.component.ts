import { Component, Input, OnInit } from '@angular/core';
import { ServiceType } from '../../../my-account/model/ServiceType';
import { TYPE_CHOSEN_ORDER, TYPE_CHOSEN_QUOTATION } from '../choose-type/choose-type.component';

@Component({
  selector: 'checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.css']
})
export class CheckoutComponent implements OnInit {

  @Input() serviceTypesSelected: ServiceType[] | undefined;
  @Input() typeChosen: string | undefined;

  constructor() { }

  ngOnInit() {
  }

  isAnOrder() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_ORDER;
  }

  isAQuotation() {
    return this.typeChosen && this.typeChosen == TYPE_CHOSEN_QUOTATION;
  }
}
