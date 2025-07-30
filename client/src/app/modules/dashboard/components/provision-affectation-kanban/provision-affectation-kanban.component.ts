import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { EChartsOption } from 'echarts';
import { Observable, of } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN } from 'src/app/libs/Constants';
import { formatDateFrance, formatDateUs } from 'src/app/libs/FormatHelper';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { PrintLabelDialogComponent } from 'src/app/modules/quotation/components/print-label-dialog/print-label-dialog.component';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { CustomerOrderAssignationStatistics } from 'src/app/modules/quotation/model/CustomerOrderAssignationStatistics';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { QuotationService } from 'src/app/modules/quotation/services/quotation.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from '../../../invoicing/components/invoice-tools';
import { CustomerOrderAssignationService } from '../../../quotation/services/customer.assignation.service';
import { AffectationEmployee } from '../../model/AffectationEmployee';
import { SwimlaneType } from '../../model/SwimlaneType';
import { AffectationEmployeeService } from '../../services/affectation.employee.service';
import { KanbanComponent } from '../kanban/kanban.component';


@Component({
  selector: 'provision-affectation-kanban',
  templateUrl: './provision-affectation-kanban.component.html',
  styleUrls: ['./provision-affectation-kanban.component.css']
})
export class ProvisionAffectationKanbanComponent extends KanbanComponent<CustomerOrder, AffectationEmployee<CustomerOrder>> implements OnInit {
  @ViewChild('bottom') private bottom!: ElementRef;
  employeesSelected: Employee | undefined;
  filterText: string = '';
  adFormalistes = this.constantService.getActiveDirectoryGroupFormalites();
  adInsertions = this.constantService.getActiveDirectoryGroupInsertions();
  orderNotification: Notification[] | undefined;
  defaultAffectation: AffectationEmployee<CustomerOrder> = { id: 0, code: 'NOT_ASSIGNED', firstname: 'A affecter', lastname: '', label: 'A affecter', } as AffectationEmployee<CustomerOrder>;

  assignationStatisticsFormalite: CustomerOrderAssignationStatistics[] = [];
  assignationStatisticsInsertions: CustomerOrderAssignationStatistics[] = [];

  chartOptions: EChartsOption = {};
  chartOptions2: EChartsOption = {};
  allEmployees: Employee[] = [];

  isDisplayStatistics: boolean = false;


  constructor(
    private orderService: CustomerOrderService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private userPreferenceService: UserPreferenceService,
    private quotationService: QuotationService,
    public mailLabelDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private notificationService: NotificationService,
    private habilitationService: HabilitationsService,
    private affectationEmployeeService: AffectationEmployeeService,
    private employeeService: EmployeeService,
    private customerOrderAssignationService: CustomerOrderAssignationService
  ) {
    super();
  }

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord");

    this.swimlaneTypes.push({ fieldName: "productionEffectiveDateTime", label: "Date", valueFonction: ((order: CustomerOrder) => formatDateUs(order.productionEffectiveDateTime)), fieldValueFunction: ((order: CustomerOrder) => formatDateUs(order.productionEffectiveDateTime)) });
    this.swimlaneTypes.push({ fieldName: "responsable.formalisteEmployee.id", label: "Formaliste", valueFonction: ((order: CustomerOrder) => (order.responsable && order.responsable.formalisteEmployee ? (order.responsable.formalisteEmployee.firstname + ' ' + order.responsable.formalisteEmployee.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "customerOrderAssignations", label: "Assignation", valueFonction: ((order: CustomerOrder) => (order.customerOrderAssignations ? order.customerOrderAssignations.filter(a => !a.isAssigned).map(a => a.assignationType.label).join(" / ") : '')), fieldValueFunction: ((order: CustomerOrder) => (order.customerOrderAssignations ? order.customerOrderAssignations.filter(a => !a.isAssigned).map(a => a.assignationType.label).join(" / ") : '')) });
    this.swimlaneTypes.push({ fieldName: "responsable.insertionEmployee.id", label: "Publisciste", valueFonction: ((order: CustomerOrder) => (order.responsable && order.responsable.insertionEmployee ? (order.responsable.insertionEmployee.firstname + ' ' + order.responsable.insertionEmployee.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "isPriority", label: "Prioritaire", valueFonction: ((order: CustomerOrder) => (order.isPriority ? 'Est prioritaire' : 'Non prioritaire')), fieldValueFunction: undefined });
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


    this.customerOrderAssignationService.getCustomerOrderAssignationStatisticsForFormalistes().subscribe(response => {
      this.employeeService.getEmployees().subscribe(allEmployee => {
        this.allEmployees = allEmployee;
        this.assignationStatisticsFormalite = response;
        this.generateChartFormalite();

        this.customerOrderAssignationService.getCustomerOrderAssignationStatisticsForInsertions().subscribe(responseI => {
          this.assignationStatisticsInsertions = responseI;
          this.generateChartInsertions();
        })
      })
    })
  }

  getFrenchDateWithoutYear(date: Date) {
    return formatDateFrance(date, false);
  }

  initStatus(fetchBookmark: boolean, applyFilter: boolean, isOnlyFilterText: boolean) {
    if (this.employeesSelected)
      this.affectationEmployeeService.findTeamEmployee(this.employeesSelected).subscribe(response => {
        if (this.habilitationService.canAddAssignOrderForProduction()) {
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
        } else {
          this.possibleEntityStatus = [this.updateEmployeeCompleted(this.employeesSelected as AffectationEmployee<CustomerOrder>)];
          this.statusSelected = [this.updateEmployeeCompleted(this.employeesSelected as AffectationEmployee<CustomerOrder>)];
        }

        if (fetchBookmark) {
          let bookmarkOrderEmployees = this.userPreferenceService.getUserSearchBookmark("kanban-affectation-employee") as Employee;
          if (bookmarkOrderEmployees && this.habilitationService.canAddAssignOrderForProduction())
            this.employeesSelected = bookmarkOrderEmployees;

          let bookmarkSwimlaneType = this.userPreferenceService.getUserSearchBookmark("kanban-affectation-swimline-type") as SwimlaneType<CustomerOrder>;
          if (bookmarkSwimlaneType) {
            for (let swimlaneType of this.swimlaneTypes)
              if (swimlaneType.fieldName == bookmarkSwimlaneType.fieldName)
                this.selectedSwimlaneType = swimlaneType;
          } else {
            this.selectedSwimlaneType = this.swimlaneTypes[0];
          }
        }

        if (applyFilter)
          this.applyFilter(isOnlyFilterText);
      })
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
    this.userPreferenceService.setUserSearchBookmark((this.employeesSelected != undefined) ? this.employeesSelected : null, "kanban-affectation-employee");
    this.userPreferenceService.setUserSearchBookmark(this.selectedSwimlaneType, "kanban-affectation-swimline-type");
  }

  findEntities() {
    if (this.employeesSelected)
      return this.orderService.getOrdersToAssignForFond(this.employeesSelected, !this.habilitationService.canAddAssignOrderForProduction()) as any as Observable<CustomerOrder[]>;
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
    employee.label = employee.firstname.replace("-", "") + ' ' + employee.lastname;
    employee.code = employee.lastname;
    return employee;
  }

  getCustomerOrderAssignationForCurrentEmployee(entity: CustomerOrder) {
    if (entity && entity.customerOrderAssignations && this.employeesSelected)
      for (let assignation of entity.customerOrderAssignations) {
        if ((this.employeesSelected.adPath.indexOf(this.constantService.getActiveDirectoryGroupFormalites().activeDirectoryPath) >= 0 || this.employeesSelected.adPath.indexOf("Informatique") >= 0) && assignation.assignationType.id == this.constantService.getAssignationTypeFormaliste().id)
          return assignation;
        if ((this.employeesSelected.adPath.indexOf(this.constantService.getActiveDirectoryGroupInsertions().activeDirectoryPath) >= 0 || this.employeesSelected.adPath.indexOf("Informatique") >= 0) && assignation.assignationType.id == this.constantService.getAssignationTypePublisciste().id)
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

    if (!this.habilitationService.canAddAssignOrderForProduction())
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
    return this.habilitationService.canReinitInvoicing();
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

  override isValidDropTarget(targetLabel: string): boolean {
    if (!this.habilitationService.canAddAssignOrderForProduction())
      return false;
    return super.isValidDropTarget(targetLabel);
  }

  generateChartFormalite() {
    if (!this.assignationStatisticsFormalite?.length) return;

    // Obtenir les dates de production distinctes triées
    const dates = Array.from(
      new Set(this.assignationStatisticsFormalite.map(d => new Date(d.productionDate).toISOString().split('T')[0]))
    ).sort();

    // Obtenir tous les employés distincts
    const employees = Array.from(
      new Set(this.assignationStatisticsFormalite.map(d => this.allEmployees.find(a => d.idEmployee == a.id)!.firstname))
    );

    // Préparer les séries : une série par employé
    const series = employees.map(employee => ({
      name: employee,
      type: 'bar',
      stack: 'total',
      data: dates.map(date => {
        const item = this.assignationStatisticsFormalite.find(d =>
          this.allEmployees.find(a => d.idEmployee == a.id)!.firstname === employee &&
          new Date(d.productionDate).toISOString().split('T')[0] === date
        );
        return item?.number ?? 0;
      }),
    })) as any;

    this.chartOptions = {
      tooltip: { trigger: 'axis' },
      legend: { data: employees },
      xAxis: {
        type: 'category',
        data: dates,
        axisLabel: { rotate: 45 },
      },
      yAxis: {
        type: 'value',
      },
      toolbox: {
        feature: {
          saveAsImage: {
            show: true,
            name: this.employeesSelected ? this.employeesSelected.lastname : "doe"
          }
        }
      },
      series,
    };
  }

  generateChartInsertions() {
    if (!this.assignationStatisticsInsertions?.length) return;

    // Obtenir les dates de production distinctes triées
    const dates = Array.from(
      new Set(this.assignationStatisticsInsertions.map(d => new Date(d.productionDate).toISOString().split('T')[0]))
    ).sort();

    // Obtenir tous les employés distincts
    const employees = Array.from(
      new Set(this.assignationStatisticsInsertions.map(d => this.allEmployees.find(a => d.idEmployee == a.id)!.firstname))
    );

    // Préparer les séries : une série par employé
    const series = employees.map(employee => ({
      name: employee,
      type: 'bar',
      stack: 'total',
      data: dates.map(date => {
        const item = this.assignationStatisticsInsertions.find(d =>
          this.allEmployees.find(a => d.idEmployee == a.id)!.firstname === employee &&
          new Date(d.productionDate).toISOString().split('T')[0] === date
        );
        return item?.number ?? 0;
      }),
    })) as any;

    this.chartOptions2 = {
      tooltip: { trigger: 'axis' },
      legend: { data: employees },
      xAxis: {
        type: 'category',
        data: dates,
        axisLabel: { rotate: 45 },
      },
      yAxis: {
        type: 'value',
      },
      toolbox: {
        feature: {
          saveAsImage: {
            show: true,
            name: this.employeesSelected ? this.employeesSelected.lastname : "doe"
          }
        }
      },
      series,
    };
  }

  displayStatistics() {
    this.isDisplayStatistics = !this.isDisplayStatistics;
  }

  scrollDown() {
    this.bottom!.nativeElement.scrollIntoView({ behavior: 'smooth' });
  }

  getComplexityColor(order: CustomerOrder) {
    if (order.complexity == 1)
      return 'warn';
    if (order.complexity == 2)
      return 'accent';
    return 'primary';
  }
}
