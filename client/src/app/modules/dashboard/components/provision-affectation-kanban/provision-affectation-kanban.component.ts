import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable, of } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { PrintLabelDialogComponent } from 'src/app/modules/quotation/components/print-label-dialog/print-label-dialog.component';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { RestUserPreferenceService } from 'src/app/services/rest.user.preference.service';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from '../../../invoicing/components/invoice-tools';
import { CustomerOrderAssignationService } from '../../../quotation/services/customer.assignation.service';
import { AffectationEmployee } from '../../model/AffectationEmployee';
import { KanbanView } from '../../model/KanbanView';
import { DEFAULT_USER_PREFERENCE } from '../../model/UserPreference';
import { AffectationEmployeeService } from '../../services/affectation.employee.service';
import { KanbanComponent, PROVISION_AFFECTATION_KANBAN } from '../kanban/kanban.component';


@Component({
  selector: 'provision-affectation-kanban',
  templateUrl: './provision-affectation-kanban.component.html',
  styleUrls: ['./provision-affectation-kanban.component.css']
})
export class ProvisionAffectationKanbanComponent extends KanbanComponent<CustomerOrder, AffectationEmployee<CustomerOrder>> implements OnInit {

  employeesSelected: Employee | undefined;
  filterText: string = '';
  adFormalistes = this.constantService.getActiveDirectoryGroupFormalites();
  adInsertions = this.constantService.getActiveDirectoryGroupInsertions();
  orderNotification: Notification[] | undefined;
  defaultAffectation: AffectationEmployee<CustomerOrder> = { id: 0, code: 'NOT_ASSIGNED', firstname: 'A affecter', lastname: '', label: 'A affecter', } as AffectationEmployee<CustomerOrder>;

  constructor(
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
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
    private affectationEmployeeService: AffectationEmployeeService,
    private employeeService: EmployeeService,
    private customerOrderAssignationService: CustomerOrderAssignationService
  ) {
    super(restUserPreferenceService2);
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

    this.employeeService.getCurrentEmployee().subscribe(currentEmployee => {
      this.employeesSelected = currentEmployee;
      this.initStatus(true, false, false);
    });
  }

  initStatus(fetchBookmark: boolean, applyFilter: boolean, isOnlyFilterText: boolean) {
    if (this.employeesSelected)
      this.affectationEmployeeService.findTeamEmployee(this.employeesSelected).subscribe(response => {
        this.possibleEntityStatus = [this.defaultAffectation];
        this.statusSelected = [this.defaultAffectation];
        for (let status of response) {
          this.possibleEntityStatus.push(this.updateEmployeeCompleted(status));
          this.statusSelected.push(this.updateEmployeeCompleted(status));
        }

        for (let status of this.possibleEntityStatus) {
          status.predecessors = this.possibleEntityStatus;
          status.successors = this.possibleEntityStatus;
        }

        for (let status of this.statusSelected) {
          status.predecessors = this.possibleEntityStatus;
          status.successors = this.possibleEntityStatus;
        }

        if (fetchBookmark) {
          this.restUserPreferenceService2.getUserPreferenceValue(this.getKanbanComponentViewCode() + "_" + DEFAULT_USER_PREFERENCE).subscribe(kanbanViewString => {
            if (kanbanViewString) {
              let kabanView: KanbanView<CustomerOrder, AffectationEmployee<CustomerOrder>>[] = JSON.parse(kanbanViewString);
              //default view so only one KanbanView
              this.employeesSelected = kabanView[0].employees[0];
              this.selectedSwimlaneType = kabanView[0].swimlaneType ? kabanView[0].swimlaneType : this.swimlaneTypes[0];
            }
          });

          if (applyFilter)
            this.applyFilter(isOnlyFilterText);
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

  setKanbanView(kanbanView: KanbanView<CustomerOrder, AffectationEmployee<CustomerOrder>>): void {
    this.labelViewSelected = kanbanView.label;
    this.statusSelected = kanbanView.status;
    this.employeesSelected = kanbanView.employees[0];
    this.selectedSwimlaneType = kanbanView.swimlaneType;
  }

  getKanbanView(): KanbanView<CustomerOrder, AffectationEmployee<CustomerOrder>> {
    return { label: this.labelViewSelected, status: this.statusSelected, employees: [this.employeesSelected], swimlaneType: this.selectedSwimlaneType } as KanbanView<CustomerOrder, AffectationEmployee<CustomerOrder>>;
  }

  getKanbanComponentViewCode(): string {
    return PROVISION_AFFECTATION_KANBAN;
  }

  findEntities() {
    if (this.employeesSelected)
      return this.orderService.getOrdersToAssignForFond(this.employeesSelected) as any as Observable<CustomerOrder[]>;
    return of([] as CustomerOrder[]);
  }

  getEntityStatus(entity: CustomerOrder): AffectationEmployee<CustomerOrder> {
    let employee = undefined;
    if (entity && entity.customerOrderAssignations && this.employeesSelected) {
      let assignation = this.getCustomerOrderAssignationForCurrentEmployee(entity);
      if (assignation)
        employee = assignation.employee as any as AffectationEmployee<CustomerOrder>;
    }

    if (employee)
      employee = this.getCompleteStatus(employee);

    if (employee)
      return this.updateEmployeeCompleted(employee);

    return this.defaultAffectation;
  }

  updateEmployeeCompleted(employee: AffectationEmployee<CustomerOrder>) {
    employee.label = employee.firstname + ' ' + employee.lastname;
    employee.code = employee.lastname;
    return employee;
  }

  getCustomerOrderAssignationForCurrentEmployee(entity: CustomerOrder) {
    if (entity && entity.customerOrderAssignations && this.employeesSelected)
      for (let assignation of entity.customerOrderAssignations) {
        if (this.employeesSelected.adPath.indexOf("Formalites") >= 0 && assignation.assignationType.id == this.constantService.getAssignationTypeFormaliste().id)
          return assignation;
        if (this.employeesSelected.adPath.indexOf("Insertions") >= 0 && assignation.assignationType.id == this.constantService.getAssignationTypePublisciste().id)
          return assignation;
      }
    return undefined;
  }

  getCompleteStatus(status: AffectationEmployee<CustomerOrder>) {
    if (this.possibleEntityStatus && this.employeesSelected)
      return this.possibleEntityStatus.filter(s => s.id == status.id)[0];
    return null;
  }

  changeEntityStatus(entity: CustomerOrder, toStatus: AffectationEmployee<CustomerOrder>): void {
    if (!this.possibleEntityStatus)
      return;

    let assignation = this.getCustomerOrderAssignationForCurrentEmployee(entity);


    if (assignation)
      if (toStatus.id == this.defaultAffectation.id)
        this.customerOrderAssignationService.updateCustomerOrderAssignation(assignation.id, undefined).subscribe(response => {
          assignation!.employee = toStatus;
          this.startFilter(true);
        });
      else
        this.customerOrderAssignationService.updateCustomerOrderAssignation(assignation.id, toStatus.id).subscribe(response => {
          assignation!.employee = toStatus;
          this.startFilter(true);
        });
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


  startFilter(isOnlyFilterText = false) {
    if (this.employeesSelected) {
      clearTimeout(this.debounce);
      this.debounce = setTimeout(() => {
        this.initStatus(false, true, isOnlyFilterText);
      }, 100);
    }
  }

}
