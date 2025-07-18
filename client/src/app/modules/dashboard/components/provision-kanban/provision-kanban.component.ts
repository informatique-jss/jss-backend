import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatExpansionPanel } from '@angular/material/expansion';
import { combineLatest, map } from 'rxjs';
import { ANNOUNCEMENT_PUBLISHED, ANNOUNCEMENT_STATUS_DONE, ANNOUNCEMENT_STATUS_IN_PROGRESS, ANNOUNCEMENT_STATUS_WAITING_CONFRERE, ANNOUNCEMENT_STATUS_WAITING_READ_CUSTOMER, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { getResponsableLabelIQuotation, getTiersLabelIQuotation } from 'src/app/modules/invoicing/components/invoice-tools';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { ChooseCompetentAuthorityDialogComponent } from 'src/app/modules/quotation/components/choose-competent-authority-dialog/choose-competent-authority-dialog.component';
import { ProvisionComponent } from 'src/app/modules/quotation/components/provision/provision.component';
import { MissingAttachmentMailDialogComponent } from 'src/app/modules/quotation/components/select-attachment-type-dialog/missing-attachment-mail-dialog.component';
import { SelectAttachmentsDialogComponent } from 'src/app/modules/quotation/components/select-attachments-dialog/select-attachment-dialog.component';
import { Announcement } from 'src/app/modules/quotation/model/Announcement';
import { AnnouncementStatus } from 'src/app/modules/quotation/model/AnnouncementStatus';
import { CustomerOrder } from 'src/app/modules/quotation/model/CustomerOrder';
import { CustomerOrderStatus } from 'src/app/modules/quotation/model/CustomerOrderStatus';
import { DomiciliationStatus } from 'src/app/modules/quotation/model/DomiciliationStatus';
import { FormaliteStatus } from 'src/app/modules/quotation/model/FormaliteStatus';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { Service } from 'src/app/modules/quotation/model/Service';
import { SimpleProvisionStatus } from 'src/app/modules/quotation/model/SimpleProvisonStatus';
import { AffaireService } from 'src/app/modules/quotation/services/affaire.service';
import { AnnouncementService } from 'src/app/modules/quotation/services/announcement.service';
import { AnnouncementStatusService } from 'src/app/modules/quotation/services/announcement.status.service';
import { CustomerOrderService } from 'src/app/modules/quotation/services/customer.order.service';
import { CustomerOrderStatusService } from 'src/app/modules/quotation/services/customer.order.status.service';
import { DomiciliationStatusService } from 'src/app/modules/quotation/services/domiciliation-status.service';
import { DomiciliationService } from 'src/app/modules/quotation/services/domiciliation.service';
import { FormaliteStatusService } from 'src/app/modules/quotation/services/formalite.status.service';
import { MissingAttachmentQueryService } from 'src/app/modules/quotation/services/missing-attachment-query.service';
import { ProvisionService } from 'src/app/modules/quotation/services/provision.service';
import { SimpleProvisionStatusService } from 'src/app/modules/quotation/services/simple.provision.status.service';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { AssoAffaireOrderService } from '../../../quotation/services/asso.affaire.order.service';
import { SwimlaneType } from '../../model/SwimlaneType';
import { AssignNewOrderDialogComponent } from '../assign-new-order-dialog/assign-new-order-dialog.component';
import { KanbanComponent } from '../kanban/kanban.component';

@Component({
  selector: 'provision-kanban',
  templateUrl: './provision-kanban.component.html',
  styleUrls: ['./provision-kanban.component.css'],
  viewProviders: [MatExpansionPanel]
})
export class ProvisionKanbanComponent extends KanbanComponent<Provision, IWorkflowElement<any>> implements OnInit {

  employeesSelected: Employee[] = [];
  filterText: string = '';
  adGroupFormalistes = this.constantService.getActiveDirectoryGroupFormalites();
  adGroupInsertions = this.constantService.getActiveDirectoryGroupInsertions();
  confrereJssSpel = this.constantService.getConfrereJssSpel();
  journalTypeSpel = this.constantService.getJournalTypeSpel();
  registrationAct = this.constantService.getProvisionTypeRegistrationAct();

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  simpleProvisionStatus: SimpleProvisionStatus[] = [] as Array<SimpleProvisionStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;

  possibleEntityStatusDeduplicated: IWorkflowElement<any>[] | undefined;
  computeAggregatedStatus = true;
  isDisplayOrderLevel = false;
  customerOrderFetched: CustomerOrder | undefined;
  possibleEntityStatusCustomerOrder: CustomerOrderStatus[] = [];


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
    private announcementService: AnnouncementService,
    public workflowDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public attachmentsDialog: MatDialog,
    public assignNewOrderDialog: MatDialog,
    public chooseCompetentAuthorityDialog: MatDialog,
    private affaireService: AffaireService,
    private missingAttachmentMailDialog: MatDialog,
    private missingAttachmentQueryService: MissingAttachmentQueryService,
    private domiciliationService: DomiciliationService,
    private orderService: CustomerOrderService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private assoAffaireOrderService: AssoAffaireOrderService,
  ) {
    super();
  }

  kanbanForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Tableau de bord");

    this.swimlaneTypes.push({ fieldName: "assignedTo.id", label: "Formaliste", valueFonction: ((provision: Provision) => (provision.assignedTo ? (provision.assignedTo.firstname + ' ' + provision.assignedTo.lastname) : '')), fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.customerOrder.responsable.id", label: "Responsable", valueFonction: (provision: Provision) => { return this.getResponsableLabelIQuotation(provision.service.assoAffaireOrder.customerOrder) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.customerOrder.responsable.tiers.id", label: "Tiers", valueFonction: (provision: Provision) => { return this.getTiersLabelIQuotation(provision.service.assoAffaireOrder.customerOrder) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.serviceLabelToDisplay", fieldValueFunction: undefined, label: "Service", valueFonction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.affaire.id", label: "Affaire", valueFonction: (provision: Provision) => { return provision.service.assoAffaireOrder.affaire.denomination ? provision.service.assoAffaireOrder.affaire.denomination : (provision.service.assoAffaireOrder.affaire.firstname + ' ' + provision.service.assoAffaireOrder.affaire.lastname) }, fieldValueFunction: undefined });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.customerOrder.createdDate", label: "Semaine", valueFonction: (provision: Provision) => { return formatDateFrance(this.getStartOfWeek(provision.service.assoAffaireOrder.customerOrder.createdDate)) }, fieldValueFunction: (provision: Provision) => { return formatDateFrance(this.getStartOfWeek(provision.service.assoAffaireOrder.customerOrder.createdDate)) } });
    this.swimlaneTypes.push({ fieldName: "service.assoAffaireOrder.customerOrder.id", label: "Commande", valueFonction: (provision: Provision) => { return "Commande " + provision.service.assoAffaireOrder.customerOrder.id }, fieldValueFunction: undefined });
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

      this.possibleEntityStatusDeduplicated = this.deduplicateArrayByLabel(this.possibleEntityStatus);

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

  fetchEntityAndOpenPanel(task: Provision, refreshColumn: boolean = false, openPanel = true) {
    this.selectedEntity = null;
    this.isDisplayOrderLevel = false;
    this.customerOrderFetched = undefined;
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

  fetchCustomerOrderAndOpenPanel(task: Provision, refreshColumn: boolean = false, openPanel = true) {
    this.isDisplayOrderLevel = true;
    this.customerOrderFetched = undefined;
    this.customerOrderStatusService.getCustomerOrderStatus().subscribe(response => {
      this.possibleEntityStatusCustomerOrder = response.filter(t => t.code != "OPEN");;
      this.orderService.getSingleCustomerOrder(task.service.assoAffaireOrder.customerOrder.id).subscribe(response => {
        this.customerOrderFetched = response as CustomerOrder;

        this.assoAffaireOrderService.getAssoAffaireOrdersForCustomerOrder(this.customerOrderFetched).subscribe(response => {
          this.customerOrderFetched!.assoAffaireOrders = response;
        })
      });
    });
  }

  saveUserPreferencesOnApplyFilter() {
    this.userPreferenceService.setUserSearchBookmark(this.statusSelected.map(status => status.id), "kanban-provision-status");
    this.userPreferenceService.setUserSearchBookmark((this.employeesSelected != undefined && this.employeesSelected.length > 0) ? this.employeesSelected : null, "kanban-provision-employee");
    this.userPreferenceService.setUserSearchBookmark(this.selectedSwimlaneType, "kanban-provision-swimline-type");
  }

  findEntities() {
    return this.provisionService.searchProvisions(this.employeesSelected.map(employee => employee.id), this.statusSelected.map(status => status.code!));
  }

  getEntityStatus(entity: Provision): IWorkflowElement<any> {
    if (entity.announcement)
      return this.announcementStatus.find(s => s.id == entity!.announcement!.announcementStatus.id)!;
    if (entity.formalite)
      return this.formaliteStatus.find(s => s.id == entity!.formalite!.formaliteStatus.id)!;
    if (entity.simpleProvision)
      return this.simpleProvisionStatus.find(s => s.id == entity!.simpleProvision!.simpleProvisionStatus.id)!;
    if (entity.domiciliation)
      return this.domiciliationStatus.find(s => s.id == entity!.domiciliation!.domiciliationStatus.id)!;
    return {} as IWorkflowElement<any>;
  }

  changeEntityStatus(entity: Provision, toStatus: IWorkflowElement<any>): void {
    this.changeProvisionStatus(entity, toStatus);
  }


  getResponsableLabelIQuotation = getResponsableLabelIQuotation;
  getTiersLabelIQuotation = getTiersLabelIQuotation;
  formatDateFrance = formatDateFrance;

  openOrder(event: any, order: CustomerOrder) {
    this.appService.openRoute({ ctrlKey: true }, 'order/' + order.id, undefined);
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

  /* Provision function */
  generatePublicationReceipt(announcement: Announcement, provision: Provision) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Générer le justificatif de parution ?",
        content: "Voulez vous simplement visualiser un justificatif de parution pour cette annonce ou bien la stocker sur cette dernière ?",
        closeActionText: "Visualiser",
        validationActionText: "Stocker"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult == true) {
        this.announcementService.generateAndStorePublicationReceipt(announcement, provision).subscribe(response => this.fetchEntityAndOpenPanel(provision, false, false));
      } else {
        this.announcementService.previewPublicationReceipt(announcement, provision);
      }
    });
  }


  generateProofReading(announcement: Announcement, provision: Provision) {
    this.announcementService.previewProofReading(announcement, provision);
  }

  generatePublicationFlag(announcement: Announcement, provision: Provision) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Générer le témoin de publication ?",
        content: "Voulez vous simplement visualiser un témoin de publication pour cette annonce ou bien le stocker sur cette dernière ?",
        closeActionText: "Visualiser",
        validationActionText: "Stocker"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult == true) {
        this.announcementService.generateAndStorePublicationFlag(announcement, provision).subscribe(response => this.fetchEntityAndOpenPanel(provision, false, false));
      } else {
        this.announcementService.previewPublicationFlag(announcement, provision);
      }
    });
  }

  generatePublicationReceiptMail(announcement: Announcement) {
    if (this.selectedEntity)
      this.announcementService.generatePublicationReceiptMail(this.selectedEntity.service.assoAffaireOrder.customerOrder, announcement).subscribe();
  }

  generateAnnouncementRequestToConfrereMail(announcement: Announcement) {
    if (this.selectedEntity)
      this.announcementService.generateAnnouncementRequestToConfrereMail(this.selectedEntity.service.assoAffaireOrder.customerOrder, this.selectedEntity.service.assoAffaireOrder, this.selectedEntity, announcement).subscribe();
  }

  generatePublicationFlagMail() {
    if (this.selectedEntity)
      this.announcementService.generatePublicationFlagMail(this.selectedEntity.service.assoAffaireOrder.customerOrder).subscribe();
  }

  sendRibRequestToAffaire() {
    if (this.selectedEntity)
      this.affaireService.sendRibRequestToAffaire(this.selectedEntity.service.assoAffaireOrder.affaire, this.selectedEntity.service.assoAffaireOrder).subscribe(reponse => {
        this.appService.displaySnackBar("Demande de RIB envoyée", false, 10);
      })
  }


  sendMissingAttachmentMail(service: Service) {
    const dialogRef = this.missingAttachmentMailDialog.open(MissingAttachmentMailDialogComponent, {
      width: "80%",
      height: "90%",
    });

    dialogRef.componentInstance.dialogRef.disableClose = true;
    dialogRef.componentInstance.service = service;
  }

  generateRegistrationAct() {
    if (!this.selectedEntity)
      return;

    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Générer l'enregistrement d'acte ?",
        closeActionText: "Annuler",
        validationActionText: "Valider"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult == true) {
        this.provisionService.getRegistrationActPdf(this.selectedEntity!.id);;
      }
    });
  }

  generateAttachmentsMail() {
    if (!this.selectedEntity)
      return;

    const dialogRef = this.attachmentsDialog.open(SelectAttachmentsDialogComponent, {
      maxWidth: "1000px",
    });

    dialogRef.componentInstance.assoAffaireOrder = this.selectedEntity.service.assoAffaireOrder;
    dialogRef.componentInstance.customerOrder = this.selectedEntity.service.assoAffaireOrder.customerOrder;
    dialogRef.componentInstance.provision = this.selectedEntity;

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && this.selectedEntity) {
        this.missingAttachmentQueryService.generateAttachmentsMail(dialogResult).subscribe(response => { });
      }
    });
  }

  generateDomiciliationContract(provision: Provision) {
    this.domiciliationService.generateDomiciliationContract(provision).subscribe(response => { this.fetchEntityAndOpenPanel(provision, false, false) });
  }

  downloadTrackingSheet(provision: Provision) {
    if (!provision.service.assoAffaireOrder.affaire.competentAuthority) {
      const dialogRef = this.chooseCompetentAuthorityDialog.open(ChooseCompetentAuthorityDialogComponent, {
      });
      dialogRef.componentInstance.title = "Autorité compétente manquante";
      dialogRef.componentInstance.label = "Choisir une autorité compétente pour l'affaire associée à la prestation";
      dialogRef.afterClosed().subscribe(selectedCompetentAuthority => {
        if (selectedCompetentAuthority) {
          provision.service.assoAffaireOrder.affaire.competentAuthority = selectedCompetentAuthority;
          this.affaireService.addOrUpdateAffaire(provision.service.assoAffaireOrder.affaire).subscribe(response => {
            this.provisionService.downloadTrackingSheet(provision.id);
          });
        }
      });
    }
    else
      this.provisionService.downloadTrackingSheet(provision.id);
  }



  changeStatus(status: IWorkflowElement<any>, provision: Provision) {
    if (status.code == ANNOUNCEMENT_STATUS_IN_PROGRESS)
      this.appService.displaySnackBar("Pour le moment, veuillez ouvrir la prestation pour changer son statut", false, 10);
    if (status.code == ANNOUNCEMENT_STATUS_DONE)
      this.appService.displaySnackBar("Pour le moment, veuillez ouvrir la prestation pour changer son statut", false, 10);
    if (status.code == ANNOUNCEMENT_PUBLISHED)
      this.appService.displaySnackBar("Pour le moment, veuillez ouvrir la prestation pour changer son statut", false, 10);
    if (status.code == ANNOUNCEMENT_STATUS_WAITING_CONFRERE)
      this.appService.displaySnackBar("Pour le moment, veuillez ouvrir la prestation pour changer son statut", false, 10);
    if (status.code == ANNOUNCEMENT_STATUS_WAITING_READ_CUSTOMER)
      this.appService.displaySnackBar("Pour le moment, veuillez ouvrir la prestation pour changer son statut", false, 10);

    this.provisionService.updateProvisionStatus(provision.id, status.code).subscribe(res => {
      this.fetchEntityAndOpenPanel(provision, true, false);
    })
  }

  assignNewCustomerOrder() {
    const dialogRef = this.assignNewOrderDialog.open(AssignNewOrderDialogComponent, {
      width: "50%"
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult) {
        this.startFilter();
      }
    });
  }
}
