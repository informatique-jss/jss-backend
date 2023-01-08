import { AfterContentChecked, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { ANNOUNCEMENT_STATUS_DONE, ANNOUNCEMENT_STATUS_IN_PROGRESS, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN } from 'src/app/libs/Constants';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { AppService } from 'src/app/services/app.service';
import { IWorkflowElement } from '../../../miscellaneous/model/IWorkflowElement';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Announcement } from '../../model/Announcement';
import { AnnouncementStatus } from '../../model/AnnouncementStatus';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { BodaccStatus } from '../../model/BodaccStatus';
import { DomiciliationStatus } from '../../model/DomiciliationStatus';
import { FormaliteStatus } from '../../model/FormaliteStatus';
import { Provision } from '../../model/Provision';
import { SimpleProvisionStatus } from '../../model/SimpleProvisonStatus';
import { AnnouncementService } from '../../services/announcement.service';
import { AnnouncementStatusService } from '../../services/announcement.status.service';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { AttachmentTypeMailQueryService } from '../../services/attachment-type-mail-query.service';
import { BodaccStatusService } from '../../services/bodacc.status.service';
import { DomiciliationStatusService } from '../../services/domiciliation-status.service';
import { FormaliteStatusService } from '../../services/formalite.status.service';
import { ProvisionService } from '../../services/provision.service';
import { SimpleProvisionStatusService } from '../../services/simple.provision.status.service';
import { SelectAttachmentTypeDialogComponent } from '../select-attachment-type-dialog/select-attachment-type-dialog.component';

@Component({
  selector: 'provision',
  templateUrl: './provision.component.html',
  styleUrls: ['./provision.component.css']
})
export class ProvisionComponent implements OnInit, AfterContentChecked {

  idAffaire: number | undefined;
  asso: AssoAffaireOrder = {} as AssoAffaireOrder;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  editMode: boolean = false;
  isStatusOpen: boolean = false;
  inputProvisionId: number = 0;

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  simpleProvisionStatus: SimpleProvisionStatus[] = [] as Array<SimpleProvisionStatus>;
  bodaccStatus: BodaccStatus[] = [] as Array<BodaccStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;

  confrereJssPaper = this.constantService.getConfrereJssPaper();
  confrereJssSpel = this.constantService.getConfrereJssSpel();
  journalTypePaper = this.constantService.getJournalTypePaper();
  journalTypeSpel = this.constantService.getJournalTypeSpel();

  currentProvisionWorkflow: Provision | undefined;

  constructor(
    private activatedRoute: ActivatedRoute,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private formBuilder: FormBuilder,
    private changeDetectorRef: ChangeDetectorRef,
    private provisionService: ProvisionService,
    public workflowDialog: MatDialog,
    private appService: AppService,
    public confirmationDialog: MatDialog,
    public attachmentTypeDialog: MatDialog,
    private constantService: ConstantService,
    private formaliteStatusService: FormaliteStatusService,
    private bodaccStatusService: BodaccStatusService,
    private announcementService: AnnouncementService,
    private attachmentTypeMailQueryService: AttachmentTypeMailQueryService,
    private domiciliationStatusService: DomiciliationStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private announcementStatusService: AnnouncementStatusService,
  ) { }

  affaireForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Prestation");
    this.idAffaire = this.activatedRoute.snapshot.params.id;
    this.inputProvisionId = this.activatedRoute.snapshot.params.idProvision;
    this.refreshAffaire();

    this.formaliteStatusService.getFormaliteStatus().subscribe(response => this.formaliteStatus = response);
    this.bodaccStatusService.getBodaccStatus().subscribe(response => this.bodaccStatus = response);
    this.domiciliationStatusService.getDomiciliationStatus().subscribe(response => this.domiciliationStatus = response);
    this.announcementStatusService.getAnnouncementStatus().subscribe(response => this.announcementStatus = response);
    this.simpleProvisionStatusService.getSimpleProvisionStatus().subscribe(response => this.simpleProvisionStatus = response);
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }


  refreshAffaire() {
    if (this.idAffaire)
      this.assoAffaireOrderService.getAssoAffaireOrder(this.idAffaire).subscribe(response => {
        this.asso = response;
        if (this.asso.affaire)
          this.appService.changeHeaderTitle("Prestation - " + (this.asso.affaire.denomination ? this.asso.affaire.denomination : (this.asso.affaire.firstname + " " + this.asso.affaire.lastname)));
      })
  }

  updateAssignedToForAffaire(employee: any, asso: AssoAffaireOrder) {
    this.assoAffaireOrderService.updateAssignedToForAsso(asso, employee).subscribe(response => {
      this.refreshAffaire();
    });
  }

  updateAssignedToForProvision(employee: any, provision: Provision) {
    this.provisionService.updateAssignedToForProvision(provision, employee).subscribe(response => {
      this.refreshAffaire();
    });
  }

  deleteProvision(asso: AssoAffaireOrder, provision: Provision) {
    asso.provisions.splice(asso.provisions.indexOf(provision), 1);
  }

  createProvision(asso: AssoAffaireOrder): Provision {
    if (asso && !asso.provisions)
      asso.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    asso.provisions.push(provision);
    return provision;
  }

  editAsso() {
    this.editMode = true;
  }

  saveAsso() {
    if (this.affaireForm.valid) {
      this.assoAffaireOrderService.updateAsso(this.asso).subscribe(response => {
        this.asso = response;
        this.editMode = false;
        this.appService.openRoute(null, '/provision/' + this.idAffaire, null);
      })
    } else {
      this.appService.displaySnackBar("Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : Prestations", true, 15);
    }
  }

  displayQuotation(event: any) {
    this.appService.openRoute(event, '/quotation/' + this.asso.quotation.id, null);
  }

  displayCustomerOrder(event: any) {
    this.appService.openRoute(event, '/order/' + this.asso.customerOrder.id, null);
  }

  displayAffaire(event: any) {
    this.appService.openRoute(event, '/affaire/' + this.asso.affaire.id, null);
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

  getWorkflowElementsForProvision(provision: Provision): IWorkflowElement[] {
    if (provision.announcement)
      return this.announcementStatus;
    if (provision.formalite)
      return this.formaliteStatus;
    if (provision.simpleProvision)
      return this.simpleProvisionStatus;
    if (provision.bodacc)
      return this.bodaccStatus;
    if (provision.domiciliation)
      return this.domiciliationStatus;
    return [] as Array<IWorkflowElement>;
  }

  getActiveWorkflowElementsForProvisionFn(provision: Provision) {
    return ProvisionComponent.getActiveWorkflowElementsForProvision(provision);
  }

  public static getActiveWorkflowElementsForProvision(provision: Provision): IWorkflowElement {
    if (provision.announcement)
      return provision.announcement.announcementStatus;
    if (provision.formalite)
      return provision.formalite.formaliteStatus;
    if (provision.simpleProvision)
      return provision.simpleProvision.simpleProvisionStatus;
    if (provision.bodacc)
      return provision.bodacc.bodaccStatus;
    if (provision.domiciliation)
      return provision.domiciliation.domiciliationStatus;
    return {} as IWorkflowElement;
  }

  changeStatus(status: IWorkflowElement, provision: Provision) {
    let saveAsso = true;
    if (provision.announcement) {
      provision.announcement.announcementStatus = status;
      if (status.code == ANNOUNCEMENT_STATUS_IN_PROGRESS) {
        saveAsso = false;
        const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
          maxWidth: "400px",
          data: {
            title: "Envoyer le attestation de parution ?",
            content: "Voulez vous envoyer le attestation de parution pour cette annonce ?",
            closeActionText: "Envoyer",
            validationActionText: "Ne pas envoyer"
          }
        });

        dialogRef.afterClosed().subscribe(dialogResult => {
          if (dialogResult == true || dialogResult == false) {
            if (provision.announcement) {
              provision.announcement.isPublicationReciptAlreadySent = dialogResult;
            }
          }
          this.saveAsso();
        });
      } else if (status.code == ANNOUNCEMENT_STATUS_DONE) {
        saveAsso = false;
        const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
          maxWidth: "400px",
          data: {
            title: "Envoyer le témoin de publication ?",
            content: "Voulez vous envoyer le témoin de publication pour cette annonce ?",
            closeActionText: "Envoyer",
            validationActionText: "Ne pas envoyer"
          }
        });

        dialogRef.afterClosed().subscribe(dialogResult => {
          if (dialogResult == true || dialogResult == false) {
            if (provision.announcement) {
              provision.announcement.isPublicationFlagAlreadySent = dialogResult;
            }
          }
          this.saveAsso();
        });
      }
    }
    if (provision.formalite)
      provision.formalite.formaliteStatus = status;
    if (provision.simpleProvision)
      provision.simpleProvision.simpleProvisionStatus = status;
    if (provision.bodacc)
      provision.bodacc.bodaccStatus = status;
    if (provision.domiciliation)
      provision.domiciliation.domiciliationStatus = status;

    if (saveAsso)
      this.saveAsso();
  }


  setCurrentProvisionWorkflow(provision: Provision) {
    this.currentProvisionWorkflow = provision;
  }

  generatePublicationReceipt(announcement: Announcement) {
    this.announcementService.previewPublicationReceipt(announcement);
  }

  generateProofReading(announcement: Announcement) {
    this.announcementService.previewProofReading(announcement);
  }

  generatePublicationFlag(announcement: Announcement) {
    this.announcementService.previewPublicationFlag(announcement);
  }

  generatePublicationReceiptMail(announcement: Announcement) {
    this.announcementService.generatePublicationReceiptMail(this.asso.customerOrder, announcement).subscribe();
  }

  generatePublicationFlagMail() {
    this.announcementService.generatePublicationFlagMail(this.asso.customerOrder).subscribe();
  }

  canModifyStatus() {
    if (this.asso && this.asso.customerOrder && this.asso.customerOrder.customerOrderStatus &&
      this.asso.customerOrder.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_OPEN
      && this.asso.customerOrder.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT)
      return true;
    return false;
  }

  generateAttachmentQueryMail() {
    const dialogRef = this.attachmentTypeDialog.open(SelectAttachmentTypeDialogComponent, {
      maxWidth: "1000px",
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && this.currentProvisionWorkflow) {
        this.attachmentTypeMailQueryService.generateAttachmentTypeMail(dialogResult, this.asso.customerOrder, this.currentProvisionWorkflow).subscribe(response => { });
      }
    });
  }
}
