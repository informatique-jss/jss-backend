import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from '../../../libs/appRest.service';
import { CustomerOrderComment } from '../model/CustomerOrderComment';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderCommentService extends AppRestService<CustomerOrderComment> {

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCustomerOrderCommentsForCustomer(idCustomerOrder: number) {
    return this.getList(new HttpParams().set("idCustomerOrder", idCustomerOrder), "customer-order-comments/customer");
  }

  addOrUpdateCustomerOrderComment(customerOrderComment: CustomerOrderComment) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment", customerOrderComment);
  }
}
