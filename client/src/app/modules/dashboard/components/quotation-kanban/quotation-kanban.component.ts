import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_OPEN } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { Quotation } from 'src/app/modules/quotation/model/Quotation';
import { QuotationStatus } from 'src/app/modules/quotation/model/QuotationStatus';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { QuotationStatusService } from 'src/app/modules/quotation/services/quotation-status.service';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from '../../../invoicing/components/invoice-tools';
import { SwimlaneType } from '../../model/SwimlaneType';
import { KanbanComponent } from '../kanban/kanban.component';

@Component({
  selector: 'quotation-kanban',
  templateUrl: './quotation-kanban.component.html',
  styleUrls: ['./quotation-kanban.component.css']
})
export class QuotationKanbanComponent extends KanbanComponent<Quotation, QuotationStatus> implements OnInit {

  employeesSelected: Employee[] = [];
  filterText: string = '';
  adGroupSales = this.constantService.getActiveDirectoryGroupSales();
  quotationNotification: Notification[] | undefined;

  constructor(
    private quotationStatusService: QuotationStatusService,
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

    this.swimlaneTypes.push({ fieldName: "responsable.salesEmployee.id", label: "Commercial", valueFonction: ((order: Quotation) => (order.responsable && order.responsable.salesEmployee ? (order.responsable.salesEmployee.firstname + ' ' + order.responsable.salesEmployee.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.id", label: "Responsable", valueFonction: (order: Quotation) => { return this.getResponsableLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "responsable.tiers.id", label: "Tiers", valueFonction: (order: Quotation) => { return this.getTiersLabelIQuotation(order) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "servicesList", fieldValueFunction: undefined, label: "Service", valueFonction: undefined });
    this.swimlaneTypes.push({ fieldName: "affairesList", fieldValueFunction: undefined, label: "Affaire", valueFonction: undefined });
    this.selectedSwimlaneType = this.swimlaneTypes[0];

    this.quotationStatusService.getQuotationStatus().subscribe(response => {
      this.possibleEntityStatus = response;
      this.statusSelected = [];

      // Retrieve bookmark
      let bookmarkpossibleEntityStatusIds = this.userPreferenceService.getUserSearchBookmark("kanban-quotation-status") as number[];
      if (bookmarkpossibleEntityStatusIds)
        for (let bookmarkpossibleEntityStatusId of bookmarkpossibleEntityStatusIds)
          for (let orderStatu of this.possibleEntityStatus!)
            if (bookmarkpossibleEntityStatusId == orderStatu.id)
              this.statusSelected.push(orderStatu);

      let bookmarkOrderEmployees = this.userPreferenceService.getUserSearchBookmark("kanban-quotation-employee") as Employee[];
      if (bookmarkOrderEmployees && bookmarkOrderEmployees.length > 0)
        this.employeesSelected = bookmarkOrderEmployees;

      let bookmarkSwimlaneType = this.userPreferenceService.getUserSearchBookmark("kanban-quotation-swimline-type") as SwimlaneType<CustomerOrder>;
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

  getNotificationForQuotation() {
    if (this.quotationNotification == undefined) {
      this.quotationNotification = [];
      this.notificationService.getNotificationsForQuotation(this.selectedEntity!.id).subscribe(response => this.quotationNotification = response);
    }
    return this.quotationNotification;
  }

  addNewNotification() {
    this.appService.addPersonnalNotification(() => this.quotationNotification = undefined, this.quotationNotification, undefined, undefined, undefined, undefined, this.selectedEntity!, undefined, undefined, undefined);
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }

  fetchEntityAndOpenPanel(task: Quotation, refreshColumn: boolean = false, openPanel = true) {
    this.selectedEntity = null;
    this.quotationService.getSingleQuotation(task.id).subscribe(response => {
      this.selectedEntity = response as Quotation;

      this.assoAffaireOrderService.getAssoAffaireOrdersForQuotation(this.selectedEntity).subscribe(response => {
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
    this.userPreferenceService.setUserSearchBookmark(this.statusSelected.map(status => status.id), "kanban-quotation-status");
    this.userPreferenceService.setUserSearchBookmark((this.employeesSelected != undefined && this.employeesSelected.length > 0) ? this.employeesSelected : null, "kanban-quotation-employee");
    this.userPreferenceService.setUserSearchBookmark(this.selectedSwimlaneType, "kanban-quotation-swimline-type");
  }

  findEntities() {
    return this.quotationService.searchQuotation(this.employeesSelected.map(employee => employee.id), this.statusSelected.map(status => status.id!));
  }

  getEntityStatus(entity: Quotation): QuotationStatus {
    return entity.quotationStatus;
  }

  changeEntityStatus(entity: Quotation, toStatus: QuotationStatus): void {
    this.changeQuotationStatus(entity, toStatus);
  }


  getResponsableLabelIQuotation = getResponsableLabelIQuotation;
  getTiersLabelIQuotation = getTiersLabelIQuotation;
  formatDateFrance = formatDateFrance;

  openQuotation(event: any, order: Quotation) {
    this.appService.openRoute(event, 'quotation/' + order.id, undefined);
  }


  generateQuotationConfirmationToCustomer() {
    if (this.selectedEntity)
      this.quotationService.generateQuotationMail(this.selectedEntity).subscribe(response => { });
  }

  canReinitInvoicing() {
    return this.habilitationsService.canReinitInvoicing();
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


  changeQuotationStatus(order: Quotation, targetStatus: IWorkflowElement<Quotation>) {
    if (!this.possibleEntityStatus)
      return;

    this.quotationService.updateQuotationStatus(order, targetStatus.code).subscribe(response => {
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
