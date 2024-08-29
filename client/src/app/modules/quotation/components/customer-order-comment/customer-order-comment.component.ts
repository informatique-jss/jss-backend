import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeFrance } from 'src/app/libs/FormatHelper';
import { copyObject, copyObjectList } from 'src/app/libs/GenericHelper';
import { ActiveDirectoryGroup } from 'src/app/modules/miscellaneous/model/ActiveDirectoryGroup';
import { ActiveDirectoryGroupService } from 'src/app/modules/miscellaneous/services/active.directory.group.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Employee } from '../../../profile/model/Employee';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderComment } from '../../model/CustomerOrderComment';
import { IQuotation } from '../../model/IQuotation';
import { Provision } from '../../model/Provision';
import { Quotation } from '../../model/Quotation';
import { Service } from '../../model/Service';
import { CustomerOrderCommentService } from '../../services/customer.order.comment.service';
import { ServiceService } from '../../services/service.service';

@Component({
  selector: 'customer-order-comment',
  templateUrl: './customer-order-comment.component.html',
  styleUrls: ['./customer-order-comment.component.css']
})
export class CustomerOrderCommentComponent implements OnInit {
  @Input() provision: Provision | undefined;
  @Input() customerOrder: CustomerOrder | undefined;
  @Input() quotation: Quotation | undefined;

  comments: CustomerOrderComment[] = [];
  newComment: CustomerOrderComment = {} as CustomerOrderComment;
  currentEmployee: Employee | undefined;
  adGroups: ActiveDirectoryGroup[] = [];
  isDisplayCommentInput: boolean = false;

  constructor(
    private activeDirectoryGroupService: ActiveDirectoryGroupService,
    private employeeService: EmployeeService,
    private constantService: ConstantService,
    private serviceService: ServiceService,
    private customerOrderCommentService: CustomerOrderCommentService,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService
  ) { }

  commentForm = this.formBuilder.group({
    newComment: [''],
  });

  getEmployeeBackgoundColor = this.employeeService.getEmployeeBackgoundColor;
  isEmployeeInGroupList = this.activeDirectoryGroupService.isEmployeeInGroupList;

  ngOnInit() {
    this.employeeService.getCurrentEmployee().subscribe(currentEmployee => {
      this.currentEmployee = currentEmployee;
      this.refreshComments();
    })

    this.activeDirectoryGroupService.getActiveDirectoryGroups().subscribe(response => this.adGroups = response.sort((a, b) => a.label.localeCompare(b.label)));
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.quotation || changes.provision || changes.customerOrder)
      this.refreshComments();
  }

  refreshComments() {
    this.comments = [];
    if (this.provision) {
      this.comments = copyObjectList(this.provision.customerOrderComments, false);
    } else if (this.quotation) {
      this.comments = copyObjectList(this.quotation.customerOrderComments, false);
      this.pushCommentsFromIQuotation(this.quotation);
    } else if (this.customerOrder) {
      this.comments = copyObjectList(this.customerOrder.customerOrderComments, false);
      this.pushCommentsFromIQuotation(this.customerOrder);
    }
    if (this.comments && this.currentEmployee) {
      this.comments.sort((a: CustomerOrderComment, b: CustomerOrderComment) => new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime());

      for (let comment of this.comments) {
        comment.isCurrentUserInGroup = this.activeDirectoryGroupService.isEmployeeInGroupList(this.currentEmployee, comment.activeDirectoryGroups);
      }
    }
  }

  pushCommentsFromIQuotation(quotation: IQuotation) {
    if (quotation && quotation.assoAffaireOrders) {
      for (let asso of quotation.assoAffaireOrders) {
        if (asso.services) {
          for (let service of asso.services) {
            if (service.provisions) {
              for (let provision of service.provisions) {
                if (provision.customerOrderComments) {
                  for (let comment of provision.customerOrderComments) {
                    let commentCopy = copyObject(comment, false);
                    commentCopy.provision = copyObject(provision, false);
                    commentCopy.provision.service = copyObject(service, false);
                    this.comments.push(commentCopy);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  toggleDisplayCommentInput() {
    this.isDisplayCommentInput = true;
  }

  addNewComment() {
    if (this.newComment && this.newComment.comment.replace(/<(?:.|\n)*?>/gm, ' ').length > 0) {
      if (this.newComment.id == undefined) {
        this.newComment.employee = this.currentEmployee!;
        if (this.customerOrder)
          this.newComment.customerOrder = this.customerOrder;
        if (this.quotation)
          this.newComment.quotation = this.quotation;
        if (this.provision)
          this.newComment.provision = this.provision;
      }
      this.customerOrderCommentService.addOrUpdateCustomerOrderComment(this.newComment).subscribe(response => {
        if (this.customerOrder)
          this.customerOrder.customerOrderComments = this.replaceCommentInTable(response, this.customerOrder.customerOrderComments)
        if (this.quotation)
          this.quotation.customerOrderComments = this.replaceCommentInTable(response, this.quotation.customerOrderComments)
        if (this.provision)
          this.provision.customerOrderComments = this.replaceCommentInTable(response, this.provision.customerOrderComments)
        this.newComment = {} as CustomerOrderComment;
        this.isDisplayCommentInput = false;
        this.refreshComments();
      })
    }
  }

  replaceCommentInTable(comment: CustomerOrderComment, comments: CustomerOrderComment[]) {
    if (!comments || comments.length == 0)
      comments = [] as CustomerOrderComment[];

    for (let i = 0; i < comments.length; i++) {
      if (comments[i].id == comment.id) {
        comments[i] = comment;
        return comments;
      }
    }
    comments.push(comment);
    return comments;
  }

  getProvisionLabel(provision: Provision) {
    return this.computeServiceLabel(provision.service, false) + " - " + this.computeProvisionLabel(provision);
  }

  getServiceLabel(service: Service) {
    return this.serviceService.getServiceLabel(service, false, this.constantService.getServiceTypeOther());
  }

  computeProvisionLabel(provision: Provision): string {
    let label = provision.provisionType.label;
    if (provision.announcement && provision.announcement.department)
      label += " - Département " + provision.announcement.department.code;
    return label;
  }

  computeServiceLabel(service: Service, doNotDisplayService: boolean): string {
    let label = '';
    if (!doNotDisplayService)
      label = this.getServiceLabel(service) + label;
    return label;
  }

  getTooltipDateText(comment: CustomerOrderComment): string {
    return `Le ${formatDateTimeFrance(comment.createdDateTime)}`;
  }

  isAdGroupSelectedInComment(group: ActiveDirectoryGroup, comment: CustomerOrderComment) {
    return comment && comment.activeDirectoryGroups && comment.activeDirectoryGroups.map(a => a.id).indexOf(group.id) >= 0;
  }

  toggleAdGroup(group: ActiveDirectoryGroup) {
    if (this.isAdGroupSelectedInComment(group, this.newComment))
      this.newComment.activeDirectoryGroups.splice(this.newComment.activeDirectoryGroups.map(a => a.id).indexOf(group.id), 1);
    else {
      if (!this.newComment.activeDirectoryGroups) this.newComment.activeDirectoryGroups = [];
      this.newComment.activeDirectoryGroups.push(group);
    }
  }

  editComment(comment: CustomerOrderComment) {
    this.isDisplayCommentInput = true;
    this.newComment = comment;
  }

  canEditComment(comment: CustomerOrderComment) {
    if (this.currentEmployee)
      return comment.employee.id == this.currentEmployee.id || this.habilitationService.canEditAllCustomerOrderComments();
    return false;
  }

  confirmReading(comment: CustomerOrderComment) {
    if (comment) {
      this.customerOrderCommentService.toggleCustomerOrderCommentIsRead(comment).subscribe(response => {
        comment.isRead = response.isRead;
      });
    }
  }
}
