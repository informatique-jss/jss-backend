import { Component, Input, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Responsable } from '../../../profile/model/Responsable';
import { LoginService } from '../../../profile/services/login.service';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderComment } from '../../model/CustomerOrderComment';
import { CustomerOrderCommentService } from '../../services/customer.order.comment.service';

@Component({
  selector: 'customer-order-chat',
  templateUrl: './customer-order-chat.component.html',
  styleUrls: ['./customer-order-chat.component.css'], imports: [SHARED_IMPORTS],
  standalone: true
})
export class CustomerOrderChatComponent implements OnInit {

  constructor(private customerOrderCommentService: CustomerOrderCommentService,
    private loginService: LoginService
  ) { }

  @Input() customerOrder: CustomerOrder | undefined;
  comments: CustomerOrderComment[] = [];
  isExpanded: boolean = true;
  newComment: CustomerOrderComment = { comment: '' } as CustomerOrderComment;
  currentUser: Responsable | undefined;

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
      // this.refreshComments();
    });

    this.customerOrderCommentService.comments.subscribe(res => {
      console.log('Nouveaux commentaires reçus via polling :', res);
      this.comments = res;
      this.sortComments();
    });

    if (this.customerOrder) {
      this.customerOrderCommentService.setWatchedOrder(this.customerOrder);
    }
  }

  ngOnDestroy() {
    this.customerOrderCommentService.setWatchedOrder(null);
  }

  toggleChat() {
    this.isExpanded = !this.isExpanded;
  }

  sendMessage() {
    if (this.newComment.comment.trim().length > 0) {

      if (this.newComment && this.newComment.comment.replace(/<(?:.|\n)*?>/gm, ' ').length > 0) {
        if (this.newComment.id == undefined) {
          if (this.currentUser)
            this.newComment.currentCustomer = this.currentUser;
          if (this.customerOrder)
            this.newComment.customerOrder = this.customerOrder;
          this.newComment.isFromTchat = true;
          this.newComment.isReadByCustomer = false;
        }
        this.customerOrderCommentService.addOrUpdateCustomerOrderComment(this.newComment).subscribe(response => {
          if (response)
            this.newComment.comment = '';
          this.refreshComments();
        })
      }
    }
  }

  refreshComments() {
    if (this.customerOrder)
      this.customerOrderCommentService.getCustomerOrderCommentsForCustomer(this.customerOrder.id).subscribe(response => {
        this.comments = response;
        this.sortComments()
      });
  }

  sortComments() {
    if (this.comments && this.currentUser) {
      this.comments.sort((b: CustomerOrderComment, a: CustomerOrderComment) => new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime());
    }
  }
}


