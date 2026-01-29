import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { CustomerOrderChatComponent } from '../../../main/components/customer-order-chat/customer-order-chat.component';
import { CustomerOrderDto } from '../../model/CustomerOrderDto';
import { CustomerOrderCommentService } from '../../services/customer-order-comment.service';
import { CustomerOrderService } from '../../services/customer-order.service';

@Component({
  selector: 'customer-order',
  templateUrl: './customer-order.component.html',
  styleUrls: ['./customer-order.component.css'],
  imports: [SHARED_IMPORTS, CustomerOrderChatComponent],
  standalone: true
})
export class CustomerOrderComponent implements OnInit {

  customerOrder: CustomerOrderDto | undefined;
  idCustomerOrder: number | undefined;

  constructor(
    private customerOrderService: CustomerOrderService,
    private customerOrderCommentService: CustomerOrderCommentService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.idCustomerOrder = this.activatedRoute.snapshot.params['id'] != "null" ? this.activatedRoute.snapshot.params['id'] : null;
    if (this.idCustomerOrder)
      this.customerOrderService.getCustomerOrder(this.idCustomerOrder).subscribe(response => {
        if (response) {
          this.customerOrder = response;
          this.customerOrderCommentService.setWatchedOrder(response);
        }
      });
  }


}
