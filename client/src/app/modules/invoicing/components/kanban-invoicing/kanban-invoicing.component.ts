import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { KanbanComponent } from 'src/app/modules/dashboard/components/kanban/kanban.component';
import { SwimlaneType } from 'src/app/modules/dashboard/model/SwimlaneType';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { PrintLabelDialogComponent } from 'src/app/modules/quotation/components/print-label-dialog/print-label-dialog.component';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { InvoicingBlockage } from '../../model/InvoicingBlockage';
import { InvocingStatistics } from '../../model/InvoicingStatistics';
import { InvoicingBlockageService } from '../../services/invoicing.blockage.service';
import { InvocingStatisticsService } from '../../services/invoicing.statistics.service';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from '../invoice-tools';

@Component({
  selector: 'kanban-invoicing',
  templateUrl: './kanban-invoicing.component.html',
  styleUrls: ['./kanban-invoicing.component.css']
})
export class KanbanInvoicingComponent extends KanbanComponent<CustomerOrder, InvoicingBlockage> implements OnInit {

  employeesSelected: Employee[] = [];
  filterText: string = '';
  orderNotification: Notification[] | undefined;
  defaultInvoicingBlockage: InvoicingBlockage = { id: 0, code: 'NOT_BLOCKED', label: 'A facturer' } as InvoicingBlockage;
  invocingStatistics: InvocingStatistics | undefined;

  constructor(
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,
    private quotationService: QuotationService,
    public mailLabelDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    private habilitationsService: HabilitationsService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private notificationService: NotificationService,
    private habilitationService: HabilitationsService,
    private invoicingBlockageService: InvoicingBlockageService,
    private invocingStatisticsService: InvocingStatisticsService
  ) {
    super();
  }

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord Facturation");

    this.swimlaneTypes.push({ fieldName: "invoicingEmployee.id", label: "Responsable de la facturation", valueFonction: ((order: CustomerOrder) => (order.invoicingEmployee ? (order.invoicingEmployee.firstname + ' ' + order.invoicingEmployee.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.id", label: "Responsable", valueFonction: (order: CustomerOrder) => { return this.getResponsableLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.tiers.id", label: "Tiers", valueFonction: (order: CustomerOrder) => { return this.getTiersLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "servicesList", fieldValueFunction: undefined, label: "Service", valueFonction: undefined });
    this.swimlaneTypes.push({ fieldName: "affairesList", fieldValueFunction: undefined, label: "Affaire", valueFonction: undefined });
    this.selectedSwimlaneType = this.swimlaneTypes[0];

    this.invoicingBlockageService.getInvoicingBlockages().subscribe(response => {
      this.possibleEntityStatus = [this.defaultInvoicingBlockage, ...response];
      for (let status of this.possibleEntityStatus) {
        status.predecessors = this.possibleEntityStatus;
        status.successors = this.possibleEntityStatus;
      }


      // Retrieve bookmark
      let bookmarkpossibleEntityStatusIds = this.userPreferenceService.getUserSearchBookmark("kanban-invoicing-status") as number[];
      if (bookmarkpossibleEntityStatusIds) {
        for (let bookmarkpossibleEntityStatusId of bookmarkpossibleEntityStatusIds)
          for (let orderStatu of this.possibleEntityStatus!)
            if (bookmarkpossibleEntityStatusId == orderStatu.id)
              this.statusSelected.push(orderStatu);
      } else {
        this.statusSelected = [... this.possibleEntityStatus];
      }

      let bookmarkOrderEmployees = this.userPreferenceService.getUserSearchBookmark("kanban-invoicing-employee") as Employee[];
      if (bookmarkOrderEmployees && bookmarkOrderEmployees.length > 0)
        this.employeesSelected = bookmarkOrderEmployees;
      else
        this.employeesSelected = [];

      let bookmarkSwimlaneType = this.userPreferenceService.getUserSearchBookmark("kanban-invoicing-swimline-type") as SwimlaneType<CustomerOrder>;
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

    this.invocingStatisticsService.getInvoicingStatistics().subscribe(response => this.invocingStatistics = response);
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
      if (!this.selectedEntity.invoicingBlockage)
        this.selectedEntity.invoicingBlockage = this.defaultInvoicingBlockage;

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
    this.userPreferenceService.setUserSearchBookmark(this.statusSelected.map(status => status.id), "kanban-invoicing-status");
    this.userPreferenceService.setUserSearchBookmark((this.employeesSelected != undefined && this.employeesSelected.length > 0) ? this.employeesSelected : null, "kanban-invoicing-employee");
    this.userPreferenceService.setUserSearchBookmark(this.selectedSwimlaneType, "kanban-invoicing-swimline-type");
  }

  findEntities() {
    return this.orderService.searchCustomerOrderForInvoicing(this.employeesSelected.map(employee => employee.id));
  }

  getEntityStatus(entity: CustomerOrder): InvoicingBlockage {
    return this.getCompleteStatus(entity.invoicingBlockage ? entity.invoicingBlockage : this.defaultInvoicingBlockage)!;
  }

  changeEntityStatus(entity: CustomerOrder, toStatus: InvoicingBlockage): void {
    this.changeInvoicingBlockageStatus(entity, toStatus);
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
      if (status.code == this.defaultInvoicingBlockage.code)
        dialogRef.componentInstance.fixedWorkflowElement = status;
    }
  }


  changeInvoicingBlockageStatus(order: CustomerOrder, targetStatus: IWorkflowElement<CustomerOrder>) {
    if (!this.possibleEntityStatus)
      return;

    if (targetStatus.code == this.defaultInvoicingBlockage.code)
      this.orderService.modifyInvoicingBlockage(order.id, undefined).subscribe(response => {
        this.fetchEntityAndOpenPanel(order, true, false);
      });
    else
      this.orderService.modifyInvoicingBlockage(order.id, targetStatus as InvoicingBlockage).subscribe(response => {
        this.fetchEntityAndOpenPanel(order, true, false);
      });
  }


  startFilter(isOnlyFilterText = false) {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.applyFilter(isOnlyFilterText);
    }, 100);
  }

  assignNewCustomerOrderToBilled() {
    this.orderService.assignNewCustomerOrderToBilled().subscribe(response => {
      if (response) {
        this.openOrder(null, response as CustomerOrder);
        this.startFilter();
      }
      else
        this.appService.displaySnackBar("Il n'y a plus de commande à facturer ⛱️", false, 10);
    })
  }


}
