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

  @Input() customerOrder: CustomerOrder | undefined;
  comments: CustomerOrderComment[] = [];
  isExpanded: boolean = true;
  newComment: CustomerOrderComment = { comment: '', customerOrder: {} } as CustomerOrderComment;
  currentUser: Responsable | undefined;

  constructor(private customerOrderCommentService: CustomerOrderCommentService,
    private loginService: LoginService
  ) { }

  ngOnInit() {
    this.loginService.getCurrentUser().subscribe(response => {
      this.currentUser = response;
    });

    this.customerOrderCommentService.comments.subscribe(res => {
      this.comments = res;
      this.sortComments();
      this.scrollToLastMessage()
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
    if (this.isExpanded) {
      this.scrollToLastMessage("instant");
    }
  }

  private scrollToLastMessage(behavior: ScrollBehavior = 'smooth'): void {
    setTimeout(() => {
      const el = document.getElementById(`comment-${this.comments.length - 1}`);
      if (el) {
        el.scrollIntoView({ behavior: behavior, block: 'start' });
      }
    }, 100); // Timeout so the DOM is well up to date
  }

  sendMessage() {
    if (this.newComment.comment.trim().length > 0) {

      if (this.newComment && this.newComment.comment.replace(/<(?:.|\n)*?>/gm, ' ').length > 0) {
        if (this.newComment.id == undefined) {
          if (this.customerOrder)
            this.newComment.customerOrder.id = this.customerOrder.id;
          this.newComment.isFromChat = true;
          this.newComment.isReadByCustomer = true;
        }
        this.customerOrderCommentService.addOrUpdateCustomerOrderComment(this.newComment).subscribe(response => {
          if (response) {
            this.comments.push(response);
            this.scrollToLastMessage();
          }
          this.newComment.comment = '';
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