import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ChangeEvent } from '@ckeditor/ckeditor5-angular';
import { Alignment, Bold, ClassicEditor, Clipboard, Essentials, Font, GeneralHtmlSupport, Indent, IndentBlock, Italic, Link, List, Mention, Paragraph, PasteFromOffice, RemoveFormat, Underline, Undo } from 'ckeditor5';
import { formatDateTimeFrance } from 'src/app/libs/FormatHelper';
import { ActiveDirectoryGroup } from 'src/app/modules/miscellaneous/model/ActiveDirectoryGroup';
import { ActiveDirectoryGroupService } from 'src/app/modules/miscellaneous/services/active.directory.group.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Employee } from '../../../profile/model/Employee';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderComment } from '../../model/CustomerOrderComment';
import { Provision } from '../../model/Provision';
import { Quotation } from '../../model/Quotation';
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
  @Input() isDisplayCommentInput: boolean = false;
  initialCommentValue: string = "";

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
      this.customerOrderCommentService.getCustomerOrderCommentForProvision(this.provision.id).subscribe(response => { this.comments = response; this.sortComments() });
    } else if (this.quotation) {
      this.customerOrderCommentService.getCustomerOrderCommentForQuotation(this.quotation.id).subscribe(response => { this.comments = response; this.sortComments() });
    } else if (this.customerOrder) {
      this.customerOrderCommentService.getCustomerOrderCommentForOrder(this.customerOrder.id).subscribe(response => { this.comments = response; this.sortComments() });
    }

  }

  sortComments() {
    if (this.comments && this.currentEmployee) {
      this.comments.sort((a: CustomerOrderComment, b: CustomerOrderComment) => new Date(b.createdDateTime).getTime() - new Date(a.createdDateTime).getTime());

      for (let comment of this.comments) {
        comment.isCurrentUserInGroup = this.activeDirectoryGroupService.isEmployeeInGroupList(this.currentEmployee, comment.activeDirectoryGroups);
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
    return provision.service.serviceLabelToDisplay + " - " + this.computeProvisionLabel(provision);
  }

  computeProvisionLabel(provision: Provision): string {
    let label = provision.provisionType.label;
    if (provision.announcement && provision.announcement.department)
      label += " - Département " + provision.announcement.department.code;
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
    this.initialCommentValue = this.newComment.comment;
  }

  canEditComment(comment: CustomerOrderComment) {
    if (this.currentEmployee && comment.employee)
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


  ckEditorComment = ClassicEditor;
  config = {
    toolbar: ['undo', 'redo', '|', 'fontFamily', 'fontSize', 'bold', 'italic', 'underline', 'fontColor', 'fontBackgroundColor', '|',
      'alignment:left', 'alignment:right', 'alignment:center', 'alignment:justify', '|', 'link', 'bulletedList', 'numberedList', 'outdent', 'indent', 'removeformat'
    ],
    plugins: [
      Bold, Essentials, Italic, Mention, Paragraph, Undo, Font, Alignment, Link, List, Indent, IndentBlock, RemoveFormat, Clipboard, GeneralHtmlSupport, Underline, PasteFromOffice
    ],
    htmlSupport: {
      allow: [
        {
          name: /.*/,
          attributes: true,
          classes: true,
          styles: true
        }
      ]
    },
  } as any;

  onCommentChange(event: ChangeEvent) {
    this.newComment.comment = event.editor.getData();
  }
}
