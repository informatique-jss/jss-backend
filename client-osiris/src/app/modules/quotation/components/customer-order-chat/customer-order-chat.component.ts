import { Component, Input, OnInit } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';
import { CustomerOrderComment } from '../../model/CustomerOrderComment';
import { IQuotationCommentService } from '../../services/iquotation-comment.service';

@Component({
  selector: 'customer-order-chat',
  templateUrl: './customer-order-chat.component.html',
  styleUrls: ['./customer-order-chat.component.css'], imports: [SHARED_IMPORTS, NgIcon],
  standalone: true
})
export class CustomerOrderChatComponent implements OnInit {

  @Input() iQuotationId: number | undefined;
  comments: CustomerOrderComment[] = [];
  isExpanded: boolean = true;
  newComment: CustomerOrderComment = { comment: '', employee: {}, currentCustomer: {} } as CustomerOrderComment;
  currentEmployee: Employee | undefined;

  constructor(private customerOrderCommentService: IQuotationCommentService,
    private employeeService: EmployeeService
  ) { }

  ngOnInit() {
    this.employeeService.getCurrentEmployee().subscribe((response: Employee | undefined) => {
      this.currentEmployee = response;
    });

    if (this.iQuotationId)
      this.customerOrderCommentService.setWatchedOrder(this.iQuotationId);

    this.customerOrderCommentService.comments?.subscribe((res: CustomerOrderComment[]) => {
      this.comments = res;
      this.sortComments();
      this.scrollToLastMessage();
    });
  }

  ngOnDestroy() {
    this.customerOrderCommentService.setWatchedOrder(null);
  }

  toggleChat() {
    this.isExpanded = !this.isExpanded;
    if (this.isExpanded)
      this.scrollToLastMessage("instant");
  }

  private scrollToLastMessage(behavior: ScrollBehavior = 'smooth'): void {
    setTimeout(() => {
      const el = document.getElementById(`comment-${this.comments.length - 1}`);
      if (el) el.scrollIntoView({ behavior: behavior, block: 'start' });
    }, 100); // Timeout so the DOM is well up to date
  }

  sendMessage() {
    if (this.newComment.comment.trim().length > 0) {

      if (this.newComment && this.newComment.comment.replace(/<(?:.|\n)*?>/gm, ' ').length > 0) {
        if (this.newComment.id == undefined) {
          this.newComment.employee = this.currentEmployee!;
          if (this.iQuotationId)
            this.newComment.customerOrderId = this.iQuotationId;
          this.newComment.isFromChat = true;
          this.newComment.isToDisplayToCustomer = true;
          this.newComment.isReadByCustomer = false;
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

  sortComments() {
    if (this.comments && this.currentEmployee)
      this.comments.sort((b: CustomerOrderComment, a: CustomerOrderComment) => new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime());
  }
} 