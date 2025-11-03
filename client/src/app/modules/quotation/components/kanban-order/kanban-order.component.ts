import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { KanbanComponent, ORDERING_KANBAN } from 'src/app/modules/dashboard/components/kanban/kanban.component';
import { KanbanView } from 'src/app/modules/dashboard/model/KanbanView';
import { DEFAULT_USER_PREFERENCE } from 'src/app/modules/dashboard/model/UserPreference';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from 'src/app/modules/invoicing/components/invoice-tools';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { PrintLabelDialogComponent } from 'src/app/modules/quotation/components/print-label-dialog/print-label-dialog.component';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { OrderBlockage } from 'src/app/modules/quotation/model/OrderBlockage';
import { ToOrderStatistics } from 'src/app/modules/quotation/model/ToOrderStatistics';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { OrderBlockageService } from 'src/app/modules/quotation/services/order.blockage.service';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { ToOrderStatisticsService } from 'src/app/modules/quotation/services/to.order.statistics.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { RestUserPreferenceService } from '../../../../services/rest.user.preference.service';

@Component({
  selector: 'kanban-order',
  templateUrl: './kanban-order.component.html',
  styleUrls: ['./kanban-order.component.css']
})
export class KanbanOrderComponent extends KanbanComponent<CustomerOrder, OrderBlockage> implements OnInit {

  employeesSelected: Employee[] = [];
  filterText: string = '';
  orderNotification: Notification[] | undefined;
  defaultOrderBlockage: OrderBlockage = { id: 0, code: 'NOT_BLOCKED', label: 'A créer' } as OrderBlockage;
  orderStatistics: ToOrderStatistics | undefined;

  constructor(
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private restUserPreferenceService2: RestUserPreferenceService,
    private quotationService: QuotationService,
    public mailLabelDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    private habilitationsService: HabilitationsService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private notificationService: NotificationService,
    private habilitationService: HabilitationsService,
    private orderBlockageService: OrderBlockageService,
    private toOrderStatisticsService: ToOrderStatisticsService
  ) {
    super(restUserPreferenceService2);
  }

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord Commande");

    this.swimlaneTypes.push({ fieldName: "orderingEmployee.id", label: "Responsable de la commande", valueFonction: ((order: CustomerOrder) => (order.orderingEmployee ? (order.orderingEmployee.firstname + ' ' + order.orderingEmployee.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.id", label: "Responsable", valueFonction: (order: CustomerOrder) => { return this.getResponsableLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.tiers.id", label: "Tiers", valueFonction: (order: CustomerOrder) => { return this.getTiersLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "servicesList", fieldValueFunction: undefined, label: "Service", valueFonction: undefined });
    this.swimlaneTypes.push({ fieldName: "affairesList", fieldValueFunction: undefined, label: "Affaire", valueFonction: undefined });
    this.selectedSwimlaneType = this.swimlaneTypes[0];

    this.orderBlockageService.getOrderBlockages().subscribe(response => {
      this.possibleEntityStatus = [this.defaultOrderBlockage, ...response];
      for (let status of this.possibleEntityStatus) {
        status.predecessors = this.possibleEntityStatus;
        status.successors = this.possibleEntityStatus;
      }

      // Retrieve bookmark
      this.statusSelected = [... this.possibleEntityStatus];

      this.restUserPreferenceService2.getUserPreferenceValue(this.getKanbanComponentViewCode() + "_" + DEFAULT_USER_PREFERENCE).subscribe(kanbanViewString => {
        if (kanbanViewString) {
          let kabanView: KanbanView<CustomerOrder, OrderBlockage>[] = JSON.parse(kanbanViewString);
          this.setKanbanView(kabanView[0]);
        }
      });

      if (this.possibleEntityStatus && this.statusSelected) {
        this.startFilter();
      }
    });

    this.toOrderStatisticsService.getOrderStatistics().subscribe(response => this.orderStatistics = response);
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
      if (!this.selectedEntity.orderBlockage)
        this.selectedEntity.orderBlockage = this.defaultOrderBlockage;

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

  setKanbanView(kanbanView: KanbanView<CustomerOrder, OrderBlockage>): void {
    this.labelViewSelected = kanbanView.label;

    if (this.possibleEntityStatus) {
      const statusIds = kanbanView.status.map(s => s.id);
      this.statusSelected = this.possibleEntityStatus.filter(s => statusIds.includes(s.id));
    }

    this.employeesSelected = kanbanView.employees;
    this.selectedSwimlaneType = this.swimlaneTypes.find(s => s.fieldName == kanbanView.swimlaneType.fieldName);
    this.applyFilter();
  }

  getKanbanView(): KanbanView<CustomerOrder, OrderBlockage> {
    let outStatus = [];
    if (this.statusSelected)
      for (let status of this.statusSelected)
        outStatus.push({ id: status.id } as OrderBlockage);
    return { label: this.labelViewSelected, status: outStatus, employees: this.employeesSelected, swimlaneType: this.selectedSwimlaneType } as KanbanView<CustomerOrder, OrderBlockage>;
  }

  getKanbanComponentViewCode(): string {
    return ORDERING_KANBAN;
  }

  findEntities() {
    return this.orderService.searchCustomerOrderForToOrder(this.employeesSelected.map(employee => employee.id));
  }

  getEntityStatus(entity: CustomerOrder): OrderBlockage {
    return this.getCompleteStatus(entity.orderBlockage ? entity.orderBlockage : this.defaultOrderBlockage)!;
  }

  changeEntityStatus(entity: CustomerOrder, toStatus: OrderBlockage): void {
    this.changeOrderBlockageStatus(entity, toStatus);
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

  canReinitOrder() {
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
      if (status.code == this.defaultOrderBlockage.code)
        dialogRef.componentInstance.fixedWorkflowElement = status;
    }
  }


  changeOrderBlockageStatus(order: CustomerOrder, targetStatus: IWorkflowElement<CustomerOrder>) {
    if (!this.possibleEntityStatus)
      return;

    if (targetStatus.code == this.defaultOrderBlockage.code)
      this.orderService.modifyOrderBlockage(order.id, undefined).subscribe(response => {
        this.fetchEntityAndOpenPanel(order, true, false);
      });
    else
      this.orderService.modifyOrderBlockage(order.id, targetStatus as OrderBlockage).subscribe(response => {
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
    this.orderService.assignNewCustomerOrderToOrder().subscribe(response => {
      if (response) {
        this.openOrder(null, response as CustomerOrder);
        this.startFilter();
      }
      else
        this.appService.displaySnackBar("Il n'y a plus de commande à créer ⛱️", false, 10);
    })
  }
}
