import { AfterContentChecked, ChangeDetectorRef, Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { ANNOUNCEMENT_PUBLISHED, ANNOUNCEMENT_STATUS_DONE, ANNOUNCEMENT_STATUS_IN_PROGRESS, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { AppService } from 'src/app/services/app.service';
import { ANNOUNCEMENT_STATUS_WAITING_CONFRERE } from '../../../../libs/Constants';
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
import { ConfrereService } from '../../services/confrere.service';
import { DomiciliationStatusService } from '../../services/domiciliation-status.service';
import { FormaliteStatusService } from '../../services/formalite.status.service';
import { ProvisionService } from '../../services/provision.service';
import { SimpleProvisionStatusService } from '../../services/simple.provision.status.service';
import { ChooseCompetentAuthorityDialogComponent } from '../choose-competent-authority-dialog/choose-competent-authority-dialog.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { QuotationComponent } from '../quotation/quotation.component';
import { SelectAttachmentTypeDialogComponent } from '../select-attachment-type-dialog/select-attachment-type-dialog.component';
import { SelectAttachmentsDialogComponent } from '../select-attachments-dialog/select-attachment-dialog.component';

@Component({
  selector: 'provision',
  templateUrl: './provision.component.html',
  styleUrls: ['./provision.component.css']
})
export class ProvisionComponent implements OnInit, AfterContentChecked {

  idAffaire: number | undefined;
  asso: AssoAffaireOrder = {} as AssoAffaireOrder;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;
  @ViewChildren(ProvisionItemComponent) provisionItemComponents: any;
  editMode: boolean = false;
  isStatusOpen: boolean = false;
  inputProvisionId: number = 0;

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  simpleProvisionStatus: SimpleProvisionStatus[] = [] as Array<SimpleProvisionStatus>;
  bodaccStatus: BodaccStatus[] = [] as Array<BodaccStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;

  confrereJssSpel = this.constantService.getConfrereJssSpel();
  journalTypePaper = this.constantService.getJournalTypePaper();
  journalTypeSpel = this.constantService.getJournalTypeSpel();
  getProvisionLabel = QuotationComponent.computeProvisionLabel;

  saveObservableSubscription: Subscription = new Subscription;

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
    public chooseCompetentAuthorityDialog: MatDialog,
    public attachmentTypeDialog: MatDialog,
    public attachmentsDialog: MatDialog,
    private constantService: ConstantService,
    private formaliteStatusService: FormaliteStatusService,
    private bodaccStatusService: BodaccStatusService,
    private announcementService: AnnouncementService,
    private attachmentTypeMailQueryService: AttachmentTypeMailQueryService,
    private domiciliationStatusService: DomiciliationStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private announcementStatusService: AnnouncementStatusService,
    private confrereService: ConfrereService,
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

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveAsso()
        else if (this.asso)
          this.editAsso()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
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


  displaySnakBarLockProvision() {
    this.appService.displaySnackBar("Il n'est pas possible d'ajouter ou modifier une prestation sur une commande au statut A facturer ou Facturer. Veuillez modifier le statut de la commande.", false, 15);
  }

  deleteProvision(asso: AssoAffaireOrder, provision: Provision) {
    if (this.asso.customerOrder.customerOrderStatus && (this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
      this.displaySnakBarLockProvision();
      return;
    }

    if (provision.debours && provision.debours.length > 0) {
      this.appService.displaySnackBar("Impossible de supprimer cette prestation : des débours/frais ont déjà été saisis", true, 15);
      return;
    }

    if (provision.announcement && provision.announcement.actuLegaleId)
      this.appService.displaySnackBar("Il n'est pas possible de supprimer cette prestation : elle a déjà été publiée sur ActuLégale.", false, 15);


    asso.provisions.splice(asso.provisions.indexOf(provision), 1);
  }

  createProvision(asso: AssoAffaireOrder): Provision {
    if (this.asso.customerOrder.customerOrderStatus && (this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
      this.displaySnakBarLockProvision();
      return {} as Provision;
    }
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
    this.editMode = true;
    setTimeout(() => this.checkAndSave(), 0);
  }

  checkAndSave() {
    let provisionStatus = true;
    this.provisionItemComponents.toArray().forEach((provisionComponent: { getFormStatus: () => any; }) => {
      provisionStatus = provisionStatus && provisionComponent.getFormStatus();
    });

    if (provisionStatus && this.affaireForm.valid) {
      this.editMode = false;
      this.assoAffaireOrderService.updateAsso(this.asso).subscribe(response => {
        this.asso = response;
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

  publicationReceiptFound(provision: Provision): boolean {
    if (provision && provision.attachments && provision.attachments.length > 0)
      for (let attachement of provision.attachments)
        if (attachement.attachmentType && attachement.attachmentType.id == this.constantService.getAttachmentTypePublicationReceipt().id)
          return true;
    return false;
  }

  changeStatus(status: IWorkflowElement, provision: Provision) {
    if (this.asso.customerOrder.customerOrderStatus && (this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
      this.displaySnakBarLockProvision();
      return;
    }

    let saveAsso = true;
    if (provision.announcement) {
      provision.announcement.announcementStatus = status;
      if (status.code == ANNOUNCEMENT_STATUS_IN_PROGRESS && !provision.announcement.isPublicationReciptAlreadySent && provision.announcement.confrere && provision.announcement.confrere.id == this.constantService.getConfrereJssSpel().id
        || !provision.announcement.isPublicationReciptAlreadySent && provision.announcement.confrere && provision.announcement.confrere.id != this.constantService.getConfrereJssSpel().id && this.publicationReceiptFound(provision)) {
        saveAsso = false;
        const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
          maxWidth: "400px",
          data: {
            title: "Envoyer l'attestation de parution ?",
            content: "Voulez vous envoyer l'attestation de parution pour cette annonce ?",
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
      } else if (!provision.announcement.isPublicationFlagAlreadySent &&
        (
          provision.announcement.confrere.id == this.confrereJssSpel.id && status.code == ANNOUNCEMENT_PUBLISHED
          ||
          provision.announcement.confrere.id != this.confrereJssSpel.id && status.code == ANNOUNCEMENT_STATUS_DONE
        )
      ) {
        saveAsso = false;
        const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
          maxWidth: "400px",
          data: {
            title: "Envoyer le témoin de publication ?",
            content: "Voulez vous envoyer le témoin de publication pour cette annonce une fois la date de publication atteinte ?",
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
      } else if (status.code == ANNOUNCEMENT_STATUS_WAITING_CONFRERE && !provision.announcement.isAnnouncementAlreadySentToConfrere && provision.announcement.confrere) {
        saveAsso = false;
        const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
          maxWidth: "400px",
          data: {
            title: "Envoyer au confrère ?",
            content: "Voulez vous envoyer la demande de publication de cette annonce au confrère " + provision.announcement.confrere.label + " ?",
            closeActionText: "Envoyer",
            validationActionText: "Ne pas envoyer"
          }
        });

        dialogRef.afterClosed().subscribe(dialogResult => {
          if (dialogResult == true || dialogResult == false) {
            if (provision.announcement) {
              provision.announcement.isAnnouncementAlreadySentToConfrere = dialogResult;
            }
          }
          this.saveAsso();
        });
      }
    }
    if (provision.formalite)
      provision.formalite.formaliteStatus = status;
    if (provision.simpleProvision) {
      if (status.code == SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY) {
        saveAsso = false;
        const dialogRef = this.chooseCompetentAuthorityDialog.open(ChooseCompetentAuthorityDialogComponent, {
          maxWidth: "400px",
        });

        dialogRef.componentInstance.title = "Choix de l'autorité compétente";
        dialogRef.componentInstance.label = "Veuillez choisir l'autorité compétente associée au statut " + status.label;
        dialogRef.afterClosed().subscribe(dialogResult => {
          if (dialogResult && dialogResult != false && provision.simpleProvision) {
            provision.simpleProvision.waitedCompetentAuthority = dialogResult;
            provision.simpleProvision.simpleProvisionStatus = status;
            this.saveAsso();
          }
        });
      } else {
        provision.simpleProvision.simpleProvisionStatus = status;
      }
    }
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
        this.announcementService.generateAndStorePublicationReceipt(announcement, provision).subscribe(response => this.refreshAffaire());
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
        this.announcementService.generateAndStorePublicationFlag(announcement, provision).subscribe(response => this.refreshAffaire());
      } else {
        this.announcementService.previewPublicationFlag(announcement, provision);
      }
    });
  }

  generatePublicationReceiptMail(announcement: Announcement) {
    this.announcementService.generatePublicationReceiptMail(this.asso.customerOrder, announcement).subscribe();
  }

  generateAnnouncementRequestToConfrereMail(announcement: Announcement) {
    if (this.currentProvisionWorkflow)
      this.announcementService.generateAnnouncementRequestToConfrereMail(this.asso.customerOrder, this.asso, this.currentProvisionWorkflow, announcement).subscribe();
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

  generateAttachmentsMail() {
    const dialogRef = this.attachmentsDialog.open(SelectAttachmentsDialogComponent, {
      maxWidth: "1000px",
    });

    dialogRef.componentInstance.assoAffaireOrder = this.asso;
    dialogRef.componentInstance.customerOrder = this.asso.customerOrder;
    dialogRef.componentInstance.provision = this.currentProvisionWorkflow;

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && this.currentProvisionWorkflow) {
        this.attachmentTypeMailQueryService.generateAttachmentsMail(dialogResult).subscribe(response => { });
      }
    });
  }
}
