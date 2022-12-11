import { AfterContentChecked, ChangeDetectorRef, Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN } from 'src/app/libs/Constants';
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
import { BodaccStatusService } from '../../services/bodacc.status.service';
import { DomiciliationStatusService } from '../../services/domiciliation-status.service';
import { FormaliteStatusService } from '../../services/formalite.status.service';
import { ProvisionService } from '../../services/provision.service';
import { SimpleProvisionStatusService } from '../../services/simple.provision.status.service';

@Component({
  selector: 'affaire',
  templateUrl: './affaire.component.html',
  styleUrls: ['./affaire.component.css']
})
export class AffaireComponent implements OnInit, AfterContentChecked {

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
    private constantService: ConstantService,
    private formaliteStatusService: FormaliteStatusService,
    private bodaccStatusService: BodaccStatusService,
    private announcementService: AnnouncementService,
    private domiciliationStatusService: DomiciliationStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private announcementStatusService: AnnouncementStatusService,
  ) { }

  affaireForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Affaire");
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
          this.appService.changeHeaderTitle("Affaire " + (this.asso.affaire.denomination ? this.asso.affaire.denomination : (this.asso.affaire.firstname + " " + this.asso.affaire.lastname)));
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
        this.appService.openRoute(null, '/affaire/' + this.idAffaire, null);
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
    this.appService.openRoute(event, '/referential/affaire/' + this.asso.affaire.id, null);
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
    return AffaireComponent.getActiveWorkflowElementsForProvision(provision);
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
    if (provision.announcement)
      provision.announcement.announcementStatus = status;
    if (provision.formalite)
      provision.formalite.formaliteStatus = status;
    if (provision.simpleProvision)
      provision.simpleProvision.simpleProvisionStatus = status;
    if (provision.bodacc)
      provision.bodacc.bodaccStatus = status;
    if (provision.domiciliation)
      provision.domiciliation.domiciliationStatus = status;
    this.saveAsso();
  }


  setCurrentProvisionWorkflow(provision: Provision) {
    this.currentProvisionWorkflow = provision;
  }

  generatePublicationReceipt(announcement: Announcement) {
    this.announcementService.previewPublicationReceipt(announcement);
  }

  generatePublicationFlag(announcement: Announcement) {
    this.announcementService.previewPublicationFlag(announcement);
  }

  generatePublicationReceiptMail() {
    this.announcementService.generatePublicationReceiptMail(this.asso.customerOrder).subscribe();
  }

  generatePublicationFlagMail() {
    this.announcementService.generatePublicationFlagMail(this.asso.customerOrder).subscribe();
  }
}
