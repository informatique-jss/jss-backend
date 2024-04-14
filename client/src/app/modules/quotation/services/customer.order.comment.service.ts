import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppRestService } from 'src/app/services/appRest.service';
import { CustomerOrderComment } from '../../quotation/model/CustomerOrderComment';

@Injectable({
  providedIn: 'root'
})
export class CustomerOrderCommentService extends AppRestService<CustomerOrderComment>{

  constructor(http: HttpClient) {
    super(http, "quotation");
  }

  getCustomerOrderComments() {
    return this.getList(new HttpParams(), "customer-order-comments");
  }
  
   addOrUpdateCustomerOrderComment(customerOrderComment: CustomerOrderComment) {
    return this.addOrUpdate(new HttpParams(), "customer-order-comment", customerOrderComment, "Enregistré", "Erreur lors de l'enregistrement");
  }

}
