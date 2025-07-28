import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { PrintLabelDialogComponent } from 'src/app/modules/quotation/components/print-label-dialog/print-label-dialog.component';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from '../../../invoicing/components/invoice-tools';
import { CustomerOrderStatusService } from '../../../quotation/services/customer.order.status.service';
import { SwimlaneType } from '../../model/SwimlaneType';
import { KanbanComponent } from '../kanban/kanban.component';


@Component({
  selector: 'order-kaban',
  templateUrl: './order-kaban.component.html',
  styleUrls: ['./order-kaban.component.css']
})
export class OrderKabanComponent extends KanbanComponent<CustomerOrder, CustomerOrderStatus> implements OnInit {

  employeesSelected: Employee[] = [];
  filterText: string = '';
  adGroupSales = this.constantService.getActiveDirectoryGroupSales();
  directionGroupSales = this.constantService.getActiveDirectoryGroupDirection();
  orderNotification: Notification[] | undefined;

  constructor(
    private orderService: CustomerOrderService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private userPreferenceService: UserPreferenceService,
    private quotationService: QuotationService,
    public mailLabelDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    private habilitationsService: HabilitationsService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private notificationService: NotificationService,
    private habilitationService: HabilitationsService
  ) {
    super();
  }

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord");

    this.swimlaneTypes.push({ fieldName: "responsable.salesEmployee.id", label: "Commercial", valueFonction: ((order: CustomerOrder) => (order.responsable && order.responsable.salesEmployee ? (order.responsable.salesEmployee.firstname + ' ' + order.responsable.salesEmployee.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.id", label: "Responsable", valueFonction: (order: CustomerOrder) => { return this.getResponsableLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.tiers.id", label: "Tiers", valueFonction: (order: CustomerOrder) => { return this.getTiersLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "servicesList", fieldValueFunction: undefined, label: "Service", valueFonction: undefined });
    this.swimlaneTypes.push({ fieldName: "affairesList", fieldValueFunction: undefined, label: "Affaire", valueFonction: undefined });
    this.selectedSwimlaneType = this.swimlaneTypes[0];

    this.customerOrderStatusService.getCustomerOrderStatus().subscribe(response => {
      this.possibleEntityStatus = response.filter(t => t.code != "OPEN");;
      this.statusSelected = [];

      // Retrieve bookmark
      let bookmarkpossibleEntityStatusIds = this.userPreferenceService.getUserSearchBookmark("kanban-order-status") as number[];
      if (bookmarkpossibleEntityStatusIds)
        for (let bookmarkpossibleEntityStatusId of bookmarkpossibleEntityStatusIds)
          for (let orderStatu of this.possibleEntityStatus!)
            if (bookmarkpossibleEntityStatusId == orderStatu.id)
              this.statusSelected.push(orderStatu);

      let bookmarkOrderEmployees = this.userPreferenceService.getUserSearchBookmark("kanban-order-employee") as Employee[];
      if (bookmarkOrderEmployees && bookmarkOrderEmployees.length > 0)
        this.employeesSelected = bookmarkOrderEmployees;

      let bookmarkSwimlaneType = this.userPreferenceService.getUserSearchBookmark("kanban-order-swimline-type") as SwimlaneType<CustomerOrder>;
      if (bookmarkSwimlaneType) {
        for (let swimlaneType of this.swimlaneTypes)
          if (swimlaneType.fieldName == bookmarkSwimlaneType.fieldName)
            this.selectedSwimlaneType = swimlaneType;
      } else {
        this.selectedSwimlaneType = this.swimlaneTypes[0];
      }

      if (this.possibleEntityStatus && this.statusSelected) {
        this.startFilter();
      }
    });
  }

  getNotificationForCustomerOrder() {
    if (this.orderNotification == undefined) {
      this.orderNotification = [];
      this.notificationService.getNotificationsForCustomerOrder(this.selectedEntity!.id).subscribe(response => this.orderNotification = response);
    }
    return this.orderNotification;
  }

  addNewNotification() {
    this.appService.addPersonnalNotification(() => this.orderNotification = undefined, this.orderNotification, this.selectedEntity!, undefined, undefined, undefined, undefined, undefined, undefined, undefined);
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

  fetchEntityAndOpenPanel(task: CustomerOrder, refreshColumn: boolean = false, openPanel = true) {
    this.selectedEntity = null;
    this.orderService.getSingleCustomerOrder(task.id).subscribe(response => {
      this.selectedEntity = response as CustomerOrder;

      this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.selectedEntity).subscribe(response => {
        this.selectedEntity!.assoAffaireOrders = response;
      })

      if (refreshColumn) {
        for (let i = 0; i < this.allEntities.length; i++)
          if (this.allEntities[i].id == this.selectedEntity.id) {
            this.allEntities[i] = this.selectedEntity;
            break;
          }
        this.applyFilter(true);
      }
    });
    if (openPanel)
      this.panelOpen = true;
  }

  saveUserPreferencesOnApplyFilter() {
    this.userPreferenceService.setUserSearchBookmark(this.statusSelected.map(status => status.id), "kanban-order-status");
    this.userPreferenceService.setUserSearchBookmark((this.employeesSelected != undefined && this.employeesSelected.length > 0) ? this.employeesSelected : null, "kanban-order-employee");
    this.userPreferenceService.setUserSearchBookmark(this.selectedSwimlaneType, "kanban-order-swimline-type");
  }

  findEntities() {
    return this.orderService.searchCustomerOrder(this.employeesSelected.map(employee => employee.id), this.statusSelected.map(status => status.id!));
  }

  getEntityStatus(entity: CustomerOrder): CustomerOrderStatus {
    return entity.customerOrderStatus;
  }

  changeEntityStatus(entity: CustomerOrder, toStatus: CustomerOrderStatus): void {
    this.changeOrderStatus(entity, toStatus);
  }


  getResponsableLabelIQuotation = getResponsableLabelIQuotation;
  getTiersLabelIQuotation = getTiersLabelIQuotation;
  formatDateFrance = formatDateFrance;

  openOrder(event: any, order: CustomerOrder) {
    this.appService.openRoute({ ctrlKey: true }, 'order/' + order.id, undefined);
  }


  generateCustomerOrderCreationConfirmationToCustomer() {
    if (this.selectedEntity)
      this.quotationService.generateCustomerOrderCreationConfirmationToCustomer(this.selectedEntity).subscribe(response => { });
  }

  generateInvoiceMail() {
    if (this.selectedEntity)
      this.quotationService.generateInvoicetMail(this.selectedEntity).subscribe(response => { });
  }

  generateMailingLabel() {
    if (this.selectedEntity) {
      const dialogRef = this.mailLabelDialog.open(PrintLabelDialogComponent, {
        maxWidth: "600px",
      });

      dialogRef.componentInstance.customerOrders.push(this.selectedEntity.id + "");
    }
  }

  canReinitInvoicing() {
    return this.habilitationsService.canReinitInvoicing();
  }

  reinitInvoicing() {
    if (this.selectedEntity && this.selectedEntity.id && this.selectedEntity.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_ABANDONED && this.selectedEntity.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_BILLED)
      this.orderService.reinitInvoicing(this.selectedEntity).subscribe(response => {
        this.fetchEntityAndOpenPanel(this.selectedEntity!);
      })
  }

  displayQuotationWorkflowDialog() {
    if (!this.possibleEntityStatus)
      return;

    let dialogRef = this.quotationWorkflowDialog.open(WorkflowDialogComponent, {
      width: '100%',
    });
    dialogRef.componentInstance.workflowElements = this.possibleEntityStatus;
    for (let status of this.possibleEntityStatus) {
      if (status.code == CUSTOMER_ORDER_STATUS_OPEN)
        dialogRef.componentInstance.fixedWorkflowElement = status;
      if (status.code == CUSTOMER_ORDER_STATUS_ABANDONED)
        dialogRef.componentInstance.excludedWorkflowElement = status;
    }
  }


  changeOrderStatus(order: CustomerOrder, targetStatus: IWorkflowElement<CustomerOrder>) {
    if (!this.possibleEntityStatus)
      return;

    this.orderService.updateCustomerStatus(order, targetStatus.code).subscribe(response => {
      this.fetchEntityAndOpenPanel(order, true, false);
    });
  }


  startFilter(isOnlyFilterText = false) {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.applyFilter(isOnlyFilterText);
    }, 100);
  }

}
