import { Component, Input, OnInit } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';
import { CustomerOrderComment } from '../../../quotation/model/CustomerOrderComment';
import { CustomerOrderDto } from '../../../quotation/model/CustomerOrderDto';
import { CustomerOrderCommentService } from '../../../quotation/services/customer-order-comment.service';

@Component({
  selector: 'customer-order-chat',
  templateUrl: './customer-order-chat.component.html',
  styleUrls: ['./customer-order-chat.component.css'], imports: [SHARED_IMPORTS, NgIcon],
  standalone: true
})
export class CustomerOrderChatComponent implements OnInit {

  constructor(private customerOrderCommentService: CustomerOrderCommentService,
    private employeeService: EmployeeService
  ) { }

  @Input() customerOrder: CustomerOrderDto | undefined;
  comments: CustomerOrderComment[] = [];
  isExpanded: boolean = true;
  newComment: CustomerOrderComment = { comment: '', employee: {}, currentCustomer: {} } as CustomerOrderComment;
  currentEmployee: Employee | undefined;

  ngOnInit() {
    this.employeeService.getCurrentEmployee().subscribe((response: Employee | undefined) => {
      this.currentEmployee = response;
    });

    if (this.customerOrder)
      this.customerOrderCommentService.setWatchedOrder(this.customerOrder);

    this.customerOrderCommentService.comments?.subscribe((res: CustomerOrderComment[]) => {
      this.comments = res;
      this.sortComments();
    });
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
          this.newComment.employee = this.currentEmployee!;
          if (this.customerOrder)
            this.newComment.customerOrderId = this.customerOrder.id;
          this.newComment.isFromChat = true;
          this.newComment.isReadByCustomer = false;
        }
        this.customerOrderCommentService.addOrUpdateCustomerOrderComment(this.newComment).subscribe(response => {
          if (response)
            this.newComment.comment = '';
        })
      }
    }
  }

  sortComments() {
    if (this.comments && this.currentEmployee) {
      this.comments.sort((b: CustomerOrderComment, a: CustomerOrderComment) => new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime());
    }
  }
}