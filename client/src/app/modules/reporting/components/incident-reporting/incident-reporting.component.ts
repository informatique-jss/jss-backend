import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { INCIDENT_REPORT_TO_COMPLETE } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { KanbanComponent } from 'src/app/modules/dashboard/components/kanban/kanban.component';
import { SwimlaneType } from 'src/app/modules/dashboard/model/SwimlaneType';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from 'src/app/modules/invoicing/components/invoice-tools';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { AssoAffaireOrderService } from 'src/app/modules/quotation/services/asso.affaire.order.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { CustomerOrderStatusService } from 'src/app/modules/quotation/services/customer.order.status.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { IncidentReport } from '../../model/IncidentReport';
import { IncidentReportStatus } from '../../model/IncidentReportStatus';
import { IncidentReportService } from '../../services/incident.report.service';
import { IncidentReportStatusService } from '../../services/incident.report.status.service';

@Component({
  selector: 'incident-reporting',
  templateUrl: './incident-reporting.component.html',
  styleUrls: ['./incident-reporting.component.css']
})
export class IncidentReportingComponent extends KanbanComponent<IncidentReport, IncidentReportStatus> implements OnInit {

  employeesSelected: Employee[] = [];
  filterText: string = '';
  isDisplayOrderLevel: boolean = false;
  customerOrderFetched: CustomerOrder | undefined;
  possibleEntityStatusCustomerOrder: CustomerOrderStatus[] = [];
  currentEmployee: Employee | undefined;

  constructor(
    private formBuilder: FormBuilder,
    private appService: AppService,
    private userPreferenceService: UserPreferenceService,
    public invidentWorkflowDialog: MatDialog,
    private incidentReportStatusService: IncidentReportStatusService,
    private incidentReportService: IncidentReportService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private orderService: CustomerOrderService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private employeeService: EmployeeService
  ) {
    super();
  }

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord CRI");

    this.swimlaneTypes.push({ fieldName: "assignedTo.id", label: "Responsable du CRI", valueFonction: ((incident: IncidentReport) => (incident.assignedTo ? (incident.assignedTo.firstname + ' ' + incident.assignedTo.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "incidentResponsibility.label", label: "Responsabilité", valueFonction: undefined, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "incidentCause.label", label: "Cause", valueFonction: undefined, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "incidentType.label", label: "Type", valueFonction: undefined, fieldValueFunction: undefined });
    this.selectedSwimlaneType = this.swimlaneTypes[0];

    this.employeeService.getCurrentEmployee().subscribe(employee => {
      this.currentEmployee = employee;
      this.incidentReportStatusService.getIncidentReportStatusList().subscribe(response => {
        this.possibleEntityStatus = response;

        // Retrieve bookmark
        let bookmarkpossibleEntityStatusIds = this.userPreferenceService.getUserSearchBookmark("kanban-cri-status") as number[];
        if (bookmarkpossibleEntityStatusIds) {
          for (let bookmarkpossibleEntityStatusId of bookmarkpossibleEntityStatusIds)
            for (let orderStatu of this.possibleEntityStatus!)
              if (bookmarkpossibleEntityStatusId == orderStatu.id)
                this.statusSelected.push(orderStatu);
        } else {
          this.statusSelected = [... this.possibleEntityStatus];
        }

        let bookmarkOrderEmployees = this.userPreferenceService.getUserSearchBookmark("kanban-cri-employee") as Employee[];
        if (bookmarkOrderEmployees && bookmarkOrderEmployees.length > 0)
          this.employeesSelected = bookmarkOrderEmployees;
        else if (this.currentEmployee) {
          this.employeesSelected = [this.currentEmployee];
        } else
          this.employeesSelected = [];

        let bookmarkSwimlaneType = this.userPreferenceService.getUserSearchBookmark("kanban-cri-swimline-type") as SwimlaneType<CustomerOrder>;
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
    })
  }

  fetchEntityAndOpenPanel(task: IncidentReport, refreshColumn: boolean = false, openPanel = true) {
    this.selectedEntity = task;
    this.isDisplayOrderLevel = false;
    if (refreshColumn) {
      for (let i = 0; i < this.allEntities.length; i++)
        if (this.allEntities[i].id == this.selectedEntity.id) {
          this.allEntities[i] = this.selectedEntity;
          break;
        }
      this.applyFilter(true);
    }
    if (openPanel)
      this.panelOpen = true;
  }

  saveUserPreferencesOnApplyFilter() {
    this.userPreferenceService.setUserSearchBookmark(this.statusSelected.map(status => status.id), "kanban-cri-status");
    this.userPreferenceService.setUserSearchBookmark((this.employeesSelected != undefined && this.employeesSelected.length > 0) ? this.employeesSelected : null, "kanban-cri-employee");
    this.userPreferenceService.setUserSearchBookmark(this.selectedSwimlaneType, "kanban-cri-swimline-type");
  }

  findEntities() {
    return this.incidentReportService.searchIncidentReport(this.employeesSelected.map(employee => employee.id), this.statusSelected.map(status => status.id!));
  }

  getEntityStatus(entity: IncidentReport): IncidentReportStatus {
    return this.getCompleteStatus(entity.incidentReportStatus)!;
  }

  changeEntityStatus(entity: IncidentReport, toStatus: IncidentReportStatus): void {
    this.changeInvoicingBlockageStatus(entity, toStatus);
  }

  getResponsableLabelIQuotation = getResponsableLabelIQuotation;
  getTiersLabelIQuotation = getTiersLabelIQuotation;
  formatDateFrance = formatDateFrance;


  displayIncidentWorkflowDialog() {
    if (!this.possibleEntityStatus)
      return;

    let dialogRef = this.invidentWorkflowDialog.open(WorkflowDialogComponent, {
      width: '100%',
    });
    dialogRef.componentInstance.workflowElements = this.possibleEntityStatus;
    for (let status of this.possibleEntityStatus) {
      if (status.code == INCIDENT_REPORT_TO_COMPLETE)
        dialogRef.componentInstance.fixedWorkflowElement = status;
    }
  }

  openOrder(event: any, order: CustomerOrder) {
    this.appService.openRoute({ ctrlKey: true }, 'order/' + order.id, undefined);
  }

  changeInvoicingBlockageStatus(incident: IncidentReport, targetStatus: IWorkflowElement<IncidentReport>) {
    if (!this.possibleEntityStatus)
      return;

    incident.incidentReportStatus = targetStatus;
    this.incidentReportService.addOrUpdateIncidentReport(incident).subscribe(response => {
      this.fetchEntityAndOpenPanel(incident, true, false);
    });
  }

  startFilter(isOnlyFilterText = false) {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.applyFilter(isOnlyFilterText);
    }, 100);
  }

  fetchCustomerOrderAndOpenPanel(task: IncidentReport, refreshColumn: boolean = false, openPanel = true) {
    this.isDisplayOrderLevel = true;
    this.customerOrderFetched = undefined;
    this.customerOrderStatusService.getCustomerOrderStatus().subscribe(response => {
      this.possibleEntityStatusCustomerOrder = response.filter(t => t.code != "OPEN"); // TODO remove web new website up, and every where this filter is put + line in QuotationStatus / CustomerOrderStatus
      this.orderService.getSingleCustomerOrder(task.customerOrder.id).subscribe(response => {
        this.customerOrderFetched = response as CustomerOrder;

        this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.customerOrderFetched).subscribe(response => {
          this.customerOrderFetched!.assoAffaireOrders = response;
        })
      });
    });
  }
}
