import { Component, Input, OnInit} from '@angular/core';
import { Responsable } from '../../model/Responsable';
import { CustomerOrder } from '../../../quotation/model/CustomerOrder';
import { CustomerOrderService } from '../../../quotation/services/customer.order.service';

@Component({
  selector: 'prepa-visite-customer-orders-responsable',
  templateUrl: './prepa-visite-customer-orders-responsable.component.html',
  styleUrls: ['./prepa-visite-customer-orders-responsable.component.css']
})

export class PrepaVisiteCustomerOrdersResponsableComponent implements OnInit {

  @Input() responsable: Responsable = {} as Responsable;

  customerOrders: CustomerOrder[] | undefined;

  constructor(
    private customerOrderService: CustomerOrderService,
  ) { }


  ngOnInit() {

    this.customerOrderService.getCustomerOrderByResponsableId(this.responsable.id).subscribe(response => {
      //this.customerOrders = response;
    })
  }


}


