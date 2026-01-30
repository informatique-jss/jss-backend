import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CustomerOrderCommentService } from '../../../../../../../client-myjss/src/app/modules/my-account/services/customer.order.comment.service';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ToastComponent } from '../../../../libs/toast/toast.component';
import { CustomerOrderChatComponent } from "../../../quotation/components/customer-order-chat/customer-order-chat.component";
import { CustomerOrderDto } from '../../../quotation/model/CustomerOrderDto';
import { LoadingComponent } from '../loading/loading.component';
import { VerticalLayoutComponent } from '../vertical-layout/vertical-layout.component';

@Component({
  selector: 'default',
  templateUrl: './default.component.html',
  styleUrls: ['./default.component.scss'],
  imports: [SHARED_IMPORTS, VerticalLayoutComponent, LoadingComponent, ToastComponent, CustomerOrderChatComponent],
  standalone: true
})
export class DefaultComponent implements OnInit {

  watchedCustomerOrders: CustomerOrderDto[] = [];

  constructor(
    private router: Router,
    private customerOrderCommentService: CustomerOrderCommentService
  ) { }

  ngOnInit() {
    this.customerOrderCommentService.getWatchedOrder().subscribe(order => {

    });
  }

  ngOnDestroy() {
  }
}
