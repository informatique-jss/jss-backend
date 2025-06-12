import { AfterContentChecked, ChangeDetectorRef, Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute } from '@angular/router';
import { Observable, Subscription } from 'rxjs';
import { ANNOUNCEMENT_PUBLISHED, ANNOUNCEMENT_STATUS_DONE, ANNOUNCEMENT_STATUS_IN_PROGRESS, ANNOUNCEMENT_STATUS_WAITING_READ_CUSTOMER, CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, SIMPLE_PROVISION_STATUS_WAITING_DOCUMENT_AUTHORITY } from 'src/app/libs/Constants';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { WorkflowDialogComponent } from 'src/app/modules/miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { ANNOUNCEMENT_STATUS_WAITING_CONFRERE } from '../../../../libs/Constants';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { IWorkflowElement } from '../../../miscellaneous/model/IWorkflowElement';
import { ConstantService } from '../../../miscellaneous/services/constant.service';
import { Affaire } from '../../model/Affaire';
import { Announcement } from '../../model/Announcement';
import { AnnouncementStatus } from '../../model/AnnouncementStatus';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { DomiciliationStatus } from '../../model/DomiciliationStatus';
import { FormaliteStatus } from '../../model/FormaliteStatus';
import { Provision } from '../../model/Provision';
import { Service } from '../../model/Service';
import { SimpleProvisionStatus } from '../../model/SimpleProvisonStatus';
import { AffaireService } from '../../services/affaire.service';
import { AnnouncementService } from '../../services/announcement.service';
import { AnnouncementStatusService } from '../../services/announcement.status.service';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { DomiciliationStatusService } from '../../services/domiciliation-status.service';
import { DomiciliationService } from '../../services/domiciliation.service';
import { FormaliteStatusService } from '../../services/formalite.status.service';
import { MissingAttachmentQueryService } from '../../services/missing-attachment-query.service';
import { ProvisionService } from '../../services/provision.service';
import { ServiceService } from '../../services/service.service';
import { SimpleProvisionStatusService } from '../../services/simple.provision.status.service';
import { ChooseCompetentAuthorityDialogComponent } from '../choose-competent-authority-dialog/choose-competent-authority-dialog.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { MissingAttachmentMailDialogComponent } from '../select-attachment-type-dialog/missing-attachment-mail-dialog.component';
import { SelectAttachmentsDialogComponent } from '../select-attachments-dialog/select-attachment-dialog.component';
import { SelectMultiServiceTypeDialogComponent } from '../select-multi-service-type-dialog/select-multi-service-type-dialog.component';

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
  inputServiceId: number = 0;

  announcementStatus: AnnouncementStatus[] = [] as Array<AnnouncementStatus>;
  formaliteStatus: FormaliteStatus[] = [] as Array<FormaliteStatus>;
  simpleProvisionStatus: SimpleProvisionStatus[] = [] as Array<SimpleProvisionStatus>;
  domiciliationStatus: DomiciliationStatus[] = [] as Array<DomiciliationStatus>;
  confrereJssSpel = this.constantService.getConfrereJssSpel();
  journalTypePaper = this.constantService.getJournalTypePaper();
  journalTypeSpel = this.constantService.getJournalTypeSpel();
  registrationAct = this.constantService.getProvisionTypeRegistrationAct();

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
    public missingAttachmentMailDialog: MatDialog,
    public selectAttachmentTypeDialog: MatDialog,
    public attachmentsDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    private constantService: ConstantService,
    private formaliteStatusService: FormaliteStatusService,
    private announcementService: AnnouncementService,
    private missingAttachmentQueryService: MissingAttachmentQueryService,
    private domiciliationStatusService: DomiciliationStatusService,
    private simpleProvisionStatusService: SimpleProvisionStatusService,
    private announcementStatusService: AnnouncementStatusService,
    private userPreferenceService: UserPreferenceService,
    private affaireService: AffaireService,
    private serviceService: ServiceService,
    private domiciliationService: DomiciliationService,
    private habilitationService: HabilitationsService,
    private notificationService: NotificationService
  ) { }

  affaireForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Prestation");
    this.idAffaire = this.activatedRoute.snapshot.params.id != "null" ? this.activatedRoute.snapshot.params.id : null;

    this.inputProvisionId = this.activatedRoute.snapshot.params.idProvision;
    this.inputServiceId = this.activatedRoute.snapshot.params.idService;

    if (!this.inputProvisionId && !this.inputServiceId)
      this.inputProvisionId = this.userPreferenceService.getUserExpensionPanelSelectionId("provision");

    this.refreshAffaire();

    this.formaliteStatusService.getFormaliteStatus().subscribe(response => this.formaliteStatus = response);
    this.domiciliationStatusService.getDomiciliationStatus().subscribe(response => this.domiciliationStatus = response);
    this.announcementStatusService.getAnnouncementStatus().subscribe(response => this.announcementStatus = response);
    this.simpleProvisionStatusService.getSimpleProvisionStatus().subscribe(response => this.simpleProvisionStatus = response);

    this.setOpenStatus();

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveAsso()
        else if (this.asso)
          this.editAsso()
    });
  }

  generateRegistrationAct() {
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
        this.provisionService.getRegistrationActPdf(this.inputProvisionId);;
      }
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  onExpandedChange(idProvision: number) {
    this.userPreferenceService.setUserExpensionPanelSelectionId("provision", idProvision);
  }

  refreshAffaire() {
    let promise: Observable<AssoAffaireOrder> | undefined;
    if (this.idAffaire)
      promise = this.assoAffaireOrderService.getAssoAffaireOrder(this.idAffaire);
    else if (this.inputProvisionId && this.inputProvisionId > 0)
      promise = this.assoAffaireOrderService.getAssoAffaireOrderFromProvision(this.inputProvisionId);
    else if (this.inputServiceId && this.inputServiceId > 0)
      promise = this.assoAffaireOrderService.getAssoAffaireOrderFromService(this.inputServiceId);

    if (promise)
      promise.subscribe(response => {
        this.asso = response;
        this.idAffaire = this.asso.id;
        if (this.asso.affaire)
          this.appService.changeHeaderTitle("Prestation - " + (this.asso.affaire.denomination ? this.asso.affaire.denomination : (this.asso.affaire.firstname + " " + this.asso.affaire.lastname)));
      })
  }

  updateAssignedToForAffaire(employee: any, asso: AssoAffaireOrder) {
    this.assoAffaireOrderService.updateAssignedToForAsso(asso, employee).subscribe(response => {
    });
    this.refreshAffaire();
  }

  updateAssignedToForProvision(employee: any, provision: Provision) {
    this.provisionService.updateAssignedToForProvision(provision, employee).subscribe(response => {
      this.refreshAffaire();
    });
  }

  setOpenStatus() {
    this.isStatusOpen = true;
    if (this.asso && this.asso.customerOrder && this.asso.customerOrder.customerOrderStatus)
      this.isStatusOpen = this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_OPEN
        || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BEING_PROCESSED
        || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT;
  }


  displaySnakBarLockProvision() {
    this.appService.displaySnackBar("Il n'est pas possible d'ajouter ou modifier une prestation sur une commande au statut A facturer ou Facturer. Veuillez modifier le statut de la commande.", false, 15);
  }

  deleteService(asso: AssoAffaireOrder, service: Service) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (this.asso.customerOrder && this.asso.customerOrder.customerOrderStatus && (this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
      }

    asso.services.splice(asso.services.indexOf(service), 1);
  }

  modifyService(service: Service) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Modifier le type de service",
        content: "Attention, la modification du type de service ajoutera les nouvelles prestations sans supprimer les prestations existantes. Pensez à vérifier les doublons après modification. Êtes-vous sûr de vouloir continuer ?",
        closeActionText: "Annuler",
        validationActionText: "Modifier"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult && service) {
        const dialogRef2 = this.selectServiceTypeDialog.open(SelectMultiServiceTypeDialogComponent, {
          width: "50%",
        });
        dialogRef2.componentInstance.isJustSelectServiceType = true;
        dialogRef2.afterClosed().subscribe(dialogResult => {
          if (dialogResult && service && (this.asso.quotation || this.asso.customerOrder)) {
            this.serviceService.modifyServiceType(service, dialogResult).subscribe(response => {
              if (this.asso.quotation) {
                this.displayQuotation(null);
              } else {
                this.displayCustomerOrder(null);
              }
            })
          }
        });
      }
    });
  }

  createService(asso: AssoAffaireOrder) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (this.asso.customerOrder.customerOrderStatus && (this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
      }
    if (asso && !asso.services)
      asso.services = [] as Array<Service>;

    let dialogRef = this.selectAttachmentTypeDialog.open(SelectMultiServiceTypeDialogComponent, {
      width: '50%',
    });
    dialogRef.componentInstance.affaire = asso.affaire;

    dialogRef.afterClosed().subscribe(response => {
      if (response != null) {
        asso.services.push(response);
      }
    })
  }

  deleteProvision(service: Service, provision: Provision) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (this.asso.customerOrder.customerOrderStatus && (this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
        return;
      }

    if (provision && provision.payments) {
      for (let payment of provision.payments)
        if (!payment.isCancelled) {
          this.appService.displaySnackBar("Il n'est pas possible de supprimer cette prestation : des paiements ont déjà été déclarés.", false, 15);
          return;
        }
    }

    if (provision.announcement && provision.announcement.actuLegaleId) {
      this.appService.displaySnackBar("Il n'est pas possible de supprimer cette prestation : elle a déjà été publiée sur ActuLégale.", false, 15);
      return;
    }


    service.provisions.splice(service.provisions.indexOf(provision), 1);
  }

  createProvision(service: Service,): Provision {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (this.asso.customerOrder.customerOrderStatus && (this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.asso.customerOrder.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
        return {} as Provision;
      }
    if (service && !service.provisions)
      service.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    service.provisions.push(provision);
    return provision;
  }


  duplicateProvision(service: Service, provision: Provision): Provision {
    let newProvisionDuplicated = {} as Provision;
    newProvisionDuplicated.provisionFamilyType = provision.provisionFamilyType;
    newProvisionDuplicated.provisionType = provision.provisionType;
    newProvisionDuplicated.assignedTo = provision.assignedTo;
    service.provisions.push(newProvisionDuplicated);
    return newProvisionDuplicated;
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
      this.assoAffaireOrderService.updateAsso(this.asso, false).subscribe(response => {
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

  getActiveWorkflowElementsForProvisionFn(provision: Provision) {
    return ProvisionComponent.getActiveWorkflowElementsForProvision(provision);
  }

  public static getActiveWorkflowElementsForProvision(provision: Provision): IWorkflowElement<any> {
    if (provision.announcement)
      return provision.announcement.announcementStatus;
    if (provision.formalite)
      return provision.formalite.formaliteStatus;
    if (provision.simpleProvision)
      return provision.simpleProvision.simpleProvisionStatus;
    if (provision.domiciliation)
      return provision.domiciliation.domiciliationStatus;
    return {} as IWorkflowElement<any>;
  }

  publicationReceiptFound(provision: Provision): boolean {
    if (provision && provision.attachments && provision.attachments.length > 0)
      for (let attachement of provision.attachments)
        if (attachement.attachmentType && attachement.attachmentType.id == this.constantService.getAttachmentTypePublicationReceipt().id)
          return true;
    return false;
  }

  changeStatus(status: IWorkflowElement<any>, provision: Provision) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
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
      } else if ((status.code == ANNOUNCEMENT_STATUS_DONE || status.code == ANNOUNCEMENT_PUBLISHED)) {
        if (provision.announcement.isPublicationFlagAlreadySent == null || provision.announcement.isPublicationFlagAlreadySent == undefined) {
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
            if (provision.announcement) {
              provision.announcement.isPublicationFlagAlreadySent = dialogResult;
            }

            if (!provision.isRedactedByJss) {
              const dialogRef2 = this.confirmationDialog.open(ConfirmDialogComponent, {
                maxWidth: "400px",
                data: {
                  title: "AL rédigée par le JSS ?",
                  content: "Est-ce que cette annonce a été rédigée par le JSS ?",
                  closeActionText: "Non",
                  validationActionText: "Oui"
                }
              });

              dialogRef2.afterClosed().subscribe(dialogResult2 => {
                if (dialogResult2)
                  provision!.isRedactedByJss = true;
                this.saveAsso();
              });
            } else {
              this.saveAsso();
            }
          });
        }
      } else if (status.code == ANNOUNCEMENT_STATUS_WAITING_CONFRERE && provision.announcement.confrere) {
        if (!provision.announcement.isAnnouncementAlreadySentToConfrere || !provision.announcement.firstConfrereSentMailDateTime) {
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
        } else {
          saveAsso = false;
          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Envoyer un erratum au confrère ?",
              content: "Voulez vous envoyer un erratum de cette annonce au confrère " + provision.announcement.confrere.label + " ?",
              closeActionText: "Envoyer",
              validationActionText: "Ne pas envoyer"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (dialogResult == true || dialogResult == false) {
              if (provision.announcement) {
                provision.announcement.isAnnouncementErratumAlreadySentToConfrere = dialogResult;
              }
            }
            this.saveAsso();
          });
        }
      } else if (status.code == ANNOUNCEMENT_STATUS_WAITING_READ_CUSTOMER) {
        if (!provision.announcement.isProofReadingDocument && !provision.announcement.firstClientReviewSentMailDateTime) {
          saveAsso = false;
          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Epreuve de relecture ?",
              content: "Le statut a été changé à En attente de relecture client mais l’option Epreuve de relecture" +
                " n’est pas sélectionnée. Voulez vous sélectionner cette option et envoyer le BAT ou annuler le changement de statut ?",
              closeActionText: "Annuler",
              validationActionText: "Ajouter l’option"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (provision.announcement && dialogResult == true) {
              provision.announcement.firstClientReviewSentMailDateTime = null;
              provision.announcement.isProofReadingDocument = true;
            }
            this.saveAsso();
          });
        } else if (provision.announcement.firstClientReviewSentMailDateTime) {
          saveAsso = false;
          const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
            maxWidth: "400px",
            data: {
              title: "Epreuve de relecture ?",
              content: "L'épreuve de relecture a déjà été envoyée au client. Voulez-vous la renvoyer ?",
              closeActionText: "Ne pas renvoyer",
              validationActionText: "Renvoyer"
            }
          });

          dialogRef.afterClosed().subscribe(dialogResult => {
            if (provision.announcement && dialogResult == true) {
              provision.announcement.firstClientReviewSentMailDateTime = null;
            }
            this.saveAsso();
          });
        }
      }
    }
    if (provision.formalite) {
      if (status.code == FORMALITE_STATUS_WAITING_DOCUMENT_AUTHORITY &&
        (!provision.formalite.formalitesGuichetUnique && !provision.formalite.formalitesInfogreffe)) {
        saveAsso = false;
        const dialogRef = this.chooseCompetentAuthorityDialog.open(ChooseCompetentAuthorityDialogComponent, {
          maxWidth: "400px",
        });

        dialogRef.componentInstance.title = "Choix de l'autorité compétente";
        dialogRef.componentInstance.label = "Veuillez choisir l'autorité compétente associée au statut " + status.label;
        dialogRef.afterClosed().subscribe(dialogResult => {
          if (dialogResult && dialogResult != false && provision.formalite) {
            provision.formalite.waitedCompetentAuthority = dialogResult;
            provision.formalite.formaliteStatus = status;
            this.saveAsso();
          }
        });
      } else {
        provision.formalite.formaliteStatus = status;
      }
    }
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

  onlyOneProvision() {
    return this.asso && this.asso.services && this.asso.services.length == 1 && this.asso.services[0].provisions && this.asso.services[0].provisions.length == 1;
  }

  sendMissingAttachmentMail(service: Service) {
    const dialogRef = this.missingAttachmentMailDialog.open(MissingAttachmentMailDialogComponent, {
      width: "80%",
      height: "90%",
    });

    dialogRef.componentInstance.dialogRef.disableClose = true;
    dialogRef.componentInstance.service = service;
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
        this.missingAttachmentQueryService.generateAttachmentsMail(dialogResult).subscribe(response => { });
      }
    });
  }

  sendRibRequestToAffaire() {
    this.affaireService.sendRibRequestToAffaire(this.asso.affaire, this.asso).subscribe(reponse => {
      this.appService.displaySnackBar("Demande de RIB envoyée", false, 10);
    })
  }

  public computeProvisionLabel(service: Service, provision: Provision, doNotDisplayService: boolean): string {
    let label = provision.provisionType ? (provision.provisionFamilyType.label + ' - ' + provision.provisionType.label) : '';
    if (provision.announcement && provision.announcement.department)
      label += " - Département " + provision.announcement.department.code;
    if (!doNotDisplayService)
      label = service.serviceLabelToDisplay + " - " + label;
    return label;
  }

  generateDomiciliationContract(provision: Provision) {
    this.domiciliationService.generateDomiciliationContract(provision).subscribe(response => { this.saveAsso() });
  }

  downloadTrackingSheet(provision: Provision) {
    if (!this.asso.affaire.competentAuthority) {
      const dialogRef = this.chooseCompetentAuthorityDialog.open(ChooseCompetentAuthorityDialogComponent, {

      });
      dialogRef.componentInstance.title = "Autorité compétente manquante";
      dialogRef.componentInstance.label = "Choisir une autorité compétente pour l'affaire associée à la prestation";
      dialogRef.afterClosed().subscribe(selectedCompetentAuthority => {
        if (selectedCompetentAuthority && this.currentProvisionWorkflow) {
          this.asso.affaire.competentAuthority = selectedCompetentAuthority;
          this.affaireService.addOrUpdateAffaire(this.asso.affaire).subscribe(response => {
            this.provisionService.downloadTrackingSheet(provision.id);
          });
        }
      });
    }
    else
      this.provisionService.downloadTrackingSheet(provision.id);
  }


  addNewNotificationOnAffaire(affaire: Affaire) {
    this.appService.addPersonnalNotification(() => this.affaireNotification = [], this.affaireNotification ? this.affaireNotification[affaire.id] : undefined, undefined, undefined, undefined, undefined, undefined, undefined, undefined, affaire);
  }

  addNewNotificationOnProvision(provision: Provision) {
    this.appService.addPersonnalNotification(() => this.provisionNotification = [], this.provisionNotification ? this.provisionNotification[provision.id] : undefined, undefined, provision, undefined, undefined, undefined, undefined, undefined, undefined);
  }

  addNewNotificationOnService(service: Service) {
    this.appService.addPersonnalNotification(() => this.serviceNotification = [], this.serviceNotification ? this.serviceNotification[service.id] : undefined, undefined, undefined, service, undefined, undefined, undefined, undefined, undefined);
  }

  affaireNotification: Notification[][] = [];
  provisionNotification: Notification[][] = [];
  serviceNotification: Notification[][] = [];

  getNotificationForAffaire(affaire: Affaire) {
    if (this.affaireNotification[affaire.id] == undefined) {
      this.affaireNotification[affaire.id] = [];
      this.notificationService.getNotificationsForAffaire(affaire.id).subscribe(response => this.affaireNotification[affaire.id] = response);
    }
    return this.affaireNotification[affaire.id];
  }

  getNotificationForService(service: Service) {
    if (this.serviceNotification[service.id] == undefined) {
      this.serviceNotification[service.id] = [];
      this.notificationService.getNotificationsForService(service.id).subscribe(response => this.serviceNotification[service.id] = response);
    }
    return this.serviceNotification[service.id];
  }

  getNotificationForProvision(provision: Provision) {
    if (this.provisionNotification[provision.id] == undefined) {
      this.provisionNotification[provision.id] = [];
      this.notificationService.getNotificationsForProvision(provision.id).subscribe(response => this.provisionNotification[provision.id] = response);
    }
    return this.provisionNotification[provision.id];
  }

  canDisplayNotifications() {
    return this.habilitationService.canDisplayNotifications();
  }
}
