import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { AppRestService } from '../../main/services/appRest.service';
import { CustomerOrderDto } from '../model/CustomerOrderDto';
import { CustomerOrderSearch } from '../model/CustomerOrderSearch';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderService extends AppRestService<CustomerOrderDto> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  selectedCustomerOrder: CustomerOrderDto[] = [];
  selectedCustomerOrderUnique: CustomerOrderDto | undefined;
  selectedCustomerOrderUniqueChange: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

  getCurrentSelectedCustomerOrder() {
    if (this.selectedCustomerOrder.length == 0) {
      let toParse = localStorage.getItem("selected-customer-order");
      if (toParse)
        this.selectedCustomerOrder = JSON.parse(toParse);
    }
    return this.selectedCustomerOrder;
  }

  setCurrentSelectedCustomerOrder(customerOrders: CustomerOrderDto[]) {
    this.selectedCustomerOrder = customerOrders;
    localStorage.setItem("selected-customer-order", JSON.stringify(customerOrders));
  }

  getSelectedCustomerOrderUnique() {
    return this.selectedCustomerOrderUnique;
  }

  getSelectedCustomerOrderUniqueChangeEvent() {
    return this.selectedCustomerOrderUniqueChange.asObservable();
  }

  setSelectedCustomerOrderUnique(CustomerOrderDto: CustomerOrderDto) {
    this.selectedCustomerOrderUnique = CustomerOrderDto;
    this.selectedCustomerOrderUniqueChange.next(true);
  }

  searchCustomerOrder(customerOrderSearch: CustomerOrderSearch) {
    return this.postList(new HttpParams(), "customer-order/search/v2", customerOrderSearch);
  }
}
