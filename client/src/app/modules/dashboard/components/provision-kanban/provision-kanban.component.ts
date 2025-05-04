import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { combineLatest, map } from 'rxjs';
import { QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from 'src/app/modules/invoicing/components/invoice-tools';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { ProvisionComponent } from 'src/app/modules/quotation/components/provision/provision.component';
import { AnnouncementStatus } from 'src/app/modules/quotation/model/AnnouncementStatus';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { DomiciliationStatus } from 'src/app/modules/quotation/model/DomiciliationStatus';
import { FormaliteStatus } from 'src/app/modules/quotation/model/FormaliteStatus';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { SimpleProvisionStatus } from 'src/app/modules/quotation/model/SimpleProvisonStatus';
import { AnnouncementStatusService } from 'src/app/modules/quotation/services/announcement.status.service';
import { DomiciliationStatusService } from 'src/app/modules/quotation/services/domiciliation-status.service';
import { FormaliteStatusService } from 'src/app/modules/quotation/services/formalite.status.service';
import { ProvisionService } from 'src/app/modules/quotation/services/provision.service';
import { SimpleProvisionStatusService } from 'src/app/modules/quotation/services/simple.provision.status.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { SwimlaneType } from '../../model/SwimlaneType';
import { KanbanComponent } from '../kanban/kanban.component';

@Component({
  selector: 'app-provision-kanban',
  templateUrl: './provision-kanban.component.html',
  styleUrls: ['./provision-kanban.component.css']
})
export class ProvisionKanbanComponent extends KanbanComponent<Provision, IWorkflowElement<any>> implements OnInit {

  employeesSelected: Employee[] = [];
  filterText: string = '';
  adGroupFormalistes = this.constantService.getActiveDirectoryGroupFormalites();
  adGroupInsertions = this.constantService.getActiveDirectoryGroupInsertions();

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  simpleProvisionStatus: SimpleProvisionStatus[] = [] as Array<SimpleProvisionStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;

  possibleEntityStatusDeduplicated: IWorkflowElement<any>[] | undefined;
  computeAggregatedStatus = true;

  constructor(
    private provisionService: ProvisionService,
    private formaliteStatusService: FormaliteStatusService,
    private domiciliationStatusService: DomiciliationStatusService,
    private announcementStatusService: AnnouncementStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private formBuilder: FormBuilder,
    private appService: AppService,
    private constantService: ConstantService,
    private userPreferenceService: UserPreferenceService,
    public workflowDialog: MatDialog,
  ) {
    super();
  }

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord");

    this.swimlaneTypes.push({ fieldName: "assignedTo.id", label: "Formaliste", valueFonction: ((provision: Provision) => (provision.assignedTo ? (provision.assignedTo.firstname + ' ' + provision.assignedTo.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.customerOrder.responsable.id", label: "Responsable", valueFonction: (provision: Provision) => { return this.getResponsableLabelIQuotation(provision.service.assoAffaireOrder.customerOrder) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.customerOrder.responsable.tiers.id", label: "Tiers", valueFonction: (provision: Provision) => { return this.getTiersLabelIQuotation(provision.service.assoAffaireOrder.customerOrder) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.customLabel", fieldValueFunction: undefined, label: "Service", valueFonction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.affaire.id", label: "Affaire", valueFonction: (provision: Provision) => { return provision.service.assoAffaireOrder.affaire.denomination ? provision.service.assoAffaireOrder.affaire.denomination : (provision.service.assoAffaireOrder.affaire.firstname + ' ' + provision.service.assoAffaireOrder.affaire.lastname) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.customerOrder.createdDate", label: "Semaine", valueFonction: (provision: Provision) => { return formatDateFrance(this.getStartOfWeek(provision.service.assoAffaireOrder.customerOrder.createdDate)) }, fieldValueFunction: (provision: Provision) => { return formatDateFrance(this.getStartOfWeek(provision.service.assoAffaireOrder.customerOrder.createdDate)) } });
    this.selectedSwimlaneType = this.swimlaneTypes[0];

    combineLatest([
      this.domiciliationStatusService.getDomiciliationStatus(),
      this.announcementStatusService.getAnnouncementStatus(),
      this.formaliteStatusService.getFormaliteStatus(),
      this.simpleProvisionStatusService.getSimpleProvisionStatus(),
    ]).pipe(
      map(([domiciliationStatus, announcementStatus, formaliteStatus, simpleProvisionStatus]) => ({ domiciliationStatus, announcementStatus, formaliteStatus, simpleProvisionStatus })),
    ).subscribe(response => {
      this.possibleEntityStatus = [];
      this.domiciliationStatus = response.domiciliationStatus;
      this.possibleEntityStatus.push(...response.domiciliationStatus);
      this.announcementStatus = response.announcementStatus;
      this.possibleEntityStatus.push(...response.announcementStatus);
      this.formaliteStatus = response.formaliteStatus;
      this.possibleEntityStatus.push(...response.formaliteStatus);
      this.simpleProvisionStatus = response.simpleProvisionStatus;
      this.possibleEntityStatus.push(...response.simpleProvisionStatus);

      this.possibleEntityStatusDeduplicated = this.deduplicateArrayById(this.possibleEntityStatus);

      this.statusSelected = [];



      // Retrieve bookmark
      let bookmarkpossibleEntityStatusIds = this.userPreferenceService.getUserSearchBookmark("kanban-provision-status") as number[];
      if (bookmarkpossibleEntityStatusIds)
        for (let bookmarkpossibleEntityStatusId of bookmarkpossibleEntityStatusIds)
          for (let orderStatu of this.possibleEntityStatus!)
            if (bookmarkpossibleEntityStatusId == orderStatu.id)
              this.statusSelected.push(orderStatu);

      let bookmarkOrderEmployees = this.userPreferenceService.getUserSearchBookmark("kanban-provision-employee") as Employee[];
      if (bookmarkOrderEmployees && bookmarkOrderEmployees.length > 0)
        this.employeesSelected = bookmarkOrderEmployees;

      let bookmarkSwimlaneType = this.userPreferenceService.getUserSearchBookmark("kanban-provision-swimline-type") as SwimlaneType<Provision>;
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

  getStartOfWeek(date: Date) {
    const d = new Date(date);
    const day = d.getDay();
    const diff = d.getDate() - day + (day === 0 ? -6 : 1);
    return new Date(d.setDate(diff));
  }

  getNumberEntitiesByStatusLabel(label: string): number {
    let number = 0;
    if (this.numberOfEntitiesByStatus && this.possibleEntityStatus) {
      for (let status of this.possibleEntityStatus)
        if (status.id)
          if (this.numberOfEntitiesByStatus[status.id] && status.label == label)
            number += this.numberOfEntitiesByStatus[status.id];
    }
    return number;
  }

  fetchEntityAndOpenPanel(task: Provision, refreshColumn: boolean = false, openPanel = true) {
    this.selectedEntity = null;
    this.provisionService.getSingleProvision(task.id).subscribe(response => {
      this.selectedEntity = response;

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
    this.userPreferenceService.setUserSearchBookmark(this.statusSelected.map(status => status.id), "kanban-provision-status");
    this.userPreferenceService.setUserSearchBookmark((this.employeesSelected != undefined && this.employeesSelected.length > 0) ? this.employeesSelected : null, "kanban-provision-employee");
    this.userPreferenceService.setUserSearchBookmark(this.selectedSwimlaneType, "kanban-provision-swimline-type");
  }

  findEntities() {
    return this.provisionService.searchProvisions(this.employeesSelected.map(employee => employee.id), this.statusSelected.map(status => status.id!));
  }

  getEntityStatus(entity: Provision): IWorkflowElement<any> {
    if (entity.announcement)
      return entity.announcement.announcementStatus;
    if (entity.formalite)
      return entity.formalite.formaliteStatus;
    if (entity.simpleProvision)
      return entity.simpleProvision.simpleProvisionStatus;
    if (entity.domiciliation)
      return entity.domiciliation.domiciliationStatus;
    return {} as IWorkflowElement<any>;
  }

  changeEntityStatus(entity: Provision, toStatus: IWorkflowElement<any>): void {
    this.changeProvisionStatus(entity, toStatus);
  }


  getResponsableLabelIQuotation = getResponsableLabelIQuotation;
  getTiersLabelIQuotation = getTiersLabelIQuotation;
  formatDateFrance = formatDateFrance;

  openOrder(event: any, order: CustomerOrder) {
    this.appService.openRoute(event, 'order/' + order.id, undefined);
  }

  displayProvisionWorkflowDialog(provision: Provision) {
    let dialogRef = this.workflowDialog.open(WorkflowDialogComponent, {
      width: '100%',
    });
    dialogRef.componentInstance.workflowElements = this.getWorkflowElementsForProvision(provision);
    for (let status of this.getWorkflowElementsForProvision(provision)) {
      if (status.code == QUOTATION_STATUS_OPEN)
        dialogRef.componentInstance.fixedWorkflowElement = status;
      if (status.code == QUOTATION_STATUS_ABANDONED)
        dialogRef.componentInstance.excludedWorkflowElement = status;
    }
    dialogRef.componentInstance.activeWorkflowElement = this.getActiveWorkflowElementsForProvisionFn(provision);
    dialogRef.componentInstance.title = "Workflow de la prestation";
  }

  getActiveWorkflowElementsForProvisionFn(provision: Provision) {
    return ProvisionComponent.getActiveWorkflowElementsForProvision(provision);
  }

  getWorkflowElementsForProvision(provision: Provision): IWorkflowElement<any>[] {
    if (provision.announcement)
      return this.announcementStatus;
    if (provision.formalite)
      return this.formaliteStatus;
    if (provision.simpleProvision)
      return this.simpleProvisionStatus;
    if (provision.domiciliation)
      return this.domiciliationStatus;
    return [] as Array<IWorkflowElement<any>>;
  }


  changeProvisionStatus(provision: Provision, targetStatus: IWorkflowElement<any>) {
    if (!this.possibleEntityStatus)
      return;

    this.provisionService.updateProvisionStatus(provision.id, targetStatus.code).subscribe(response => {
      this.fetchEntityAndOpenPanel(provision, true, false);
    });
  }


  startFilter(isOnlyFilterText = false) {
    clearTimeout(this.debounce);
    this.debounce = setTimeout(() => {
      this.applyFilter(isOnlyFilterText);
    }, 100);
  }
}
