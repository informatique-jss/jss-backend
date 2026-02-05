import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { CustomerOrderDto } from '../../model/CustomerOrderDto';
import { CustomerOrderService } from '../../services/customer-order.service';
import { IQuotationCommentService } from '../../services/iquotation-comment.service';

@Component({
  selector: 'customer-order',
  templateUrl: './customer-order.component.html',
  styleUrls: ['./customer-order.component.css'],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class CustomerOrderComponent implements OnInit {

  customerOrder: CustomerOrderDto | undefined;
  idCustomerOrder: number | undefined;

  constructor(
    private customerOrderService: CustomerOrderService,
    private iQuotationCommentService: IQuotationCommentService,
    private activatedRoute: ActivatedRoute,
  ) { }

  ngOnInit() {
    this.idCustomerOrder = this.activatedRoute.snapshot.params['id'] != "null" ? this.activatedRoute.snapshot.params['id'] : null;
    if (this.idCustomerOrder)
      this.customerOrderService.getCustomerOrder(this.idCustomerOrder).subscribe(response => {
        if (response) {
          this.customerOrder = response;
        }
      });
  }

  openChatForOrder() {
    if (this.customerOrder)
      this.iQuotationCommentService.openChatForOrder(this.customerOrder.id);
  }
}
