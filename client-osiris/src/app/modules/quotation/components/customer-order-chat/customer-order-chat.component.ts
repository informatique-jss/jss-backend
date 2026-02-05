import { Component, ElementRef, OnInit, QueryList, ViewChildren } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { formatDateHourFrance } from '../../../../libs/FormatHelper';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeService } from '../../../profile/services/employee.service';
import { CustomerOrderComment } from '../../model/CustomerOrderComment';
import { IQuotationCommentService } from '../../services/iquotation-comment.service';
import { QuotationService } from '../../services/quotation.service';

@Component({
  selector: 'customer-order-chat',
  templateUrl: './customer-order-chat.component.html',
  styleUrls: ['./customer-order-chat.component.css'], imports: [SHARED_IMPORTS, NgIcon],
  standalone: true
})
export class CustomerOrderChatComponent implements OnInit {

  private observer: IntersectionObserver | null = null;

  // Get all message elements from the DOM
  @ViewChildren('messageItem') messageElements!: QueryList<ElementRef>;

  commentListByIQuotation: CustomerOrderComment[][] = [];
  newComments: Map<number, CustomerOrderComment> = new Map<number, CustomerOrderComment>();
  currentEmployee: Employee | undefined;

  currentIQuotationList: number[] = [];

  pollingInterval: any;

  formatDateHourFrance = formatDateHourFrance;

  constructor(
    private iQuotationCommentService: IQuotationCommentService,
    private employeeService: EmployeeService,
    private quotationService: QuotationService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.employeeService.getCurrentEmployee().subscribe((response: Employee | undefined) => {
      this.currentEmployee = response;
    });

    this.currentIQuotationList = this.iQuotationCommentService.getWatchedIQuotations();

    for (let iQuotationId of this.currentIQuotationList)
      this.fetchCommentsForIQuotationsAndScroll(iQuotationId);

    this.iQuotationCommentService.getActiveOrderSourceObservable().subscribe(res => {
      this.currentIQuotationList = this.iQuotationCommentService.getWatchedIQuotations();
      for (let iQuotationId of this.currentIQuotationList)
        this.fetchCommentsForIQuotationsAndScroll(iQuotationId);
    });

    this.pollingInterval = setInterval(() => {
      this.fetchUnreadCommentsForEmployee();
    }, 1000);
  }

  private fetchUnreadCommentsForEmployee() {
    this.iQuotationCommentService.getUnreadCommentsForEmployee().subscribe(commentsFound => {
      for (let comment of commentsFound) {
        let workingIQuotation = comment.customerOrderId;
        if (!this.commentListByIQuotation[workingIQuotation])
          this.commentListByIQuotation[workingIQuotation] = [];
        if (!this.commentListByIQuotation[workingIQuotation].find(comm => comm.id == comment.id))
          this.commentListByIQuotation[workingIQuotation].push(comment);

        if (this.commentListByIQuotation[workingIQuotation] && this.commentListByIQuotation[workingIQuotation].length > 0)
          this.iQuotationCommentService.addToWatchedIQuotations(workingIQuotation);
      }
      this.sortComments();
    });
  }

  private fetchCommentsForIQuotationsAndScroll(iQuotationId: number) {
    this.scrollToLastMessageOfConversation(iQuotationId, 'instant');

    this.iQuotationCommentService.getCommentsFromChatForIQuotations(this.currentIQuotationList).subscribe(res => {
      this.commentListByIQuotation[iQuotationId] = res.filter(comment => comment.customerOrderId == iQuotationId);
      this.scrollToLastMessageOfConversation(iQuotationId, 'instant');
    });
  }

  toggleChat(iQuotationId: number) {
    let isExpanded = this.iQuotationCommentService.toggleIsExpanded(iQuotationId);
    if (isExpanded)
      this.fetchCommentsForIQuotationsAndScroll(iQuotationId)
  }

  private scrollToLastMessageOfConversation(idQuotation: number, behavior: ScrollBehavior = 'smooth'): void {
    let comments = this.commentListByIQuotation[idQuotation];
    if (!comments) return;

    setTimeout(() => {
      const el = document.getElementById(`comment-${idQuotation}-${comments.length - 1}`);
      if (el) el.scrollIntoView({ behavior: behavior, block: 'start' });
    }, 100); //Timeout so the DOM is well up to date
  }

  getCommentModel(iQuotationId: number): CustomerOrderComment {
    if (!this.newComments.get(iQuotationId))
      this.newComments.set(iQuotationId, { comment: '' } as CustomerOrderComment);
    return this.newComments.get(iQuotationId)!;
  }

  sendMessage(iQuotationId: number) {
    let draft = this.getCommentModel(iQuotationId);

    if (draft)
      if (draft.comment.trim().length > 0)
        if (draft && draft.comment.replace(/<(?:.|\n)*?>/gm, ' ').length > 0) {
          if (draft.id == undefined) {
            draft.employee = this.currentEmployee!;
            draft.isFromChat = true;
            draft.isToDisplayToCustomer = true;
            draft.isReadByCustomer = false;
            draft.isRead = true;
            draft.customerOrderId = iQuotationId;
          }
          this.iQuotationCommentService.addOrUpdateCustomerOrderComment(draft).subscribe(response => {
            if (response) {
              if (!this.commentListByIQuotation[iQuotationId])
                this.commentListByIQuotation[iQuotationId] = [];
              this.commentListByIQuotation[iQuotationId].push(response);
              this.scrollToLastMessageOfConversation(iQuotationId);
            }
            this.newComments.set(iQuotationId, { comment: '' } as CustomerOrderComment);
          })
        }
  }

  closeChat(iQuotationId: number) {
    this.iQuotationCommentService.removeFromWatchedIQuotations(iQuotationId);
  }

  sortComments() {
    if (this.commentListByIQuotation && this.commentListByIQuotation.length > 0 && this.currentEmployee)
      for (let comments of this.commentListByIQuotation)
        if (comments)
          comments.sort((b: CustomerOrderComment, a: CustomerOrderComment) => new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime());
  }

  getIsExpandedMap(iQuotationId: number) {
    if (this.iQuotationCommentService.getIsExpanded(iQuotationId))
      return true;
    return false;
  }

  openRouteForTiers(iQuotationId: number) {
    this.quotationService.getTiersByIQuotation(iQuotationId).subscribe((response: number) => {
      if (response > 0)
        this.appService.openRoute(null, "/tiers/view/" + response, null);
    });
  }

  getUnreadCommentsCount(idQuotationId: number): number {
    let commentsForIQuotation = this.commentListByIQuotation[idQuotationId];
    if (!commentsForIQuotation || commentsForIQuotation.length === 0)
      return 0;

    return commentsForIQuotation.filter(comment => !comment.employee || !comment.employee.id).filter(comment => comment.isRead !== true).length;
  }

  markAsRead(comment: CustomerOrderComment) {
    if (comment && !comment.isRead) {
      comment.isRead = true;
      this.iQuotationCommentService.addOrUpdateCustomerOrderComment(comment).subscribe();
    }
  }

  handleMouseEnter(event: MouseEvent) {
    const header = event.currentTarget as HTMLElement;
    const container = header.querySelector('.text-window') as HTMLElement;
    const text = header.querySelector('.scroll-text') as HTMLElement;

    if (container && text) {
      const containerWidth = container.offsetWidth;
      const textWidth = text.scrollWidth;

      if (textWidth > containerWidth) {
        const scrollDistance = (textWidth - containerWidth) + 20;
        text.style.setProperty('--scroll-x', `-${scrollDistance}px`);
        // Optional: Adjust the transition duration based on length
        const duration = scrollDistance / 50; // 50px / second
        text.style.transitionDuration = `${Math.max(duration, 1)}s`;
      }
    }
  }

  handleMouseLeave(event: MouseEvent) {
    const header = event.currentTarget as HTMLElement;
    const text = header.querySelector('.scroll-text') as HTMLElement;
    if (text)
      text.style.setProperty('--scroll-x', '0px');
  }

  ngOnDestroy() {
    this.iQuotationCommentService.emptyWatchedIQuotations();
    clearInterval(this.pollingInterval);
    if (this.observer)
      this.observer.disconnect();
  }
}