import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, INVOICING_PAYMENT_LIMIT_REFUND_EUROS, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, VALIDATED_BY_CUSTOMER } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { IReferential } from 'src/app/modules/administration/model/IReferential';
import { AssociatePaymentDialogComponent } from 'src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { IWorkflowElement } from 'src/app/modules/miscellaneous/model/IWorkflowElement';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { NotificationService } from 'src/app/modules/miscellaneous/services/notification.service';
import { IncidentReport } from 'src/app/modules/reporting/model/IncidentReport';
import { IncidentReportService } from 'src/app/modules/reporting/services/incident.report.service';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { EntityType } from 'src/app/routing/search/EntityType';
import { CUSTOMER_ORDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { SearchService } from 'src/app/services/search.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_OPEN } from '../../../../libs/Constants';
import { replaceDocument } from '../../../../libs/DocumentHelper';
import { formatDateFrance } from '../../../../libs/FormatHelper';
import { instanceOfQuotation } from '../../../../libs/TypeHelper';
import { Notification } from '../../../../modules/miscellaneous/model/Notification';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { InvoiceSearchResult } from '../../../invoicing/model/InvoiceSearchResult';
import { InvoiceSearchResultService } from '../../../invoicing/services/invoice.search.result.service';
import { WorkflowDialogComponent } from '../../../miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { Affaire } from '../../model/Affaire';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { OrderingSearch } from '../../model/OrderingSearch';
import { Provision } from '../../model/Provision';
import { QuotationStatus } from '../../model/QuotationStatus';
import { Service } from '../../model/Service';
import { VatBase } from '../../model/VatBase';
import { CustomerOrderService } from '../../services/customer.order.service';
import { CustomerOrderStatusService } from '../../services/customer.order.status.service';
import { OrderingSearchResultService } from '../../services/ordering.search.result.service';
import { ProvisionService } from '../../services/provision.service';
import { QuotationStatusService } from '../../services/quotation-status.service';
import { QuotationSearchResultService } from '../../services/quotation.search.result.service';
import { QuotationService } from '../../services/quotation.service';
import { ServiceService } from '../../services/service.service';
import { ValidationIdQuotationService } from '../../services/validation-id.quotation.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';
import { OrderSimilaritiesDialogComponent } from '../order-similarities-dialog/order-similarities-dialog.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { PrintLabelDialogComponent } from '../print-label-dialog/print-label-dialog.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { ProvisionComponent } from '../provision/provision.component';
import { QuotationAbandonReasonDialog } from '../quotation-abandon-reason-dialog/quotation-abandon-reason-dialog';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';
import { SelectMultiServiceTypeDialogComponent } from '../select-multi-service-type-dialog/select-multi-service-type-dialog.component';
import { SuggestedQuotationsDialogComponent } from '../suggested-quotations-dialog/suggested-quotations-dialog.component';
import { IQuotation } from './../../model/IQuotation';

@Component({
  selector: 'quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.css']
})
export class QuotationComponent implements OnInit, AfterContentChecked {
  quotation: IQuotation = {} as IQuotation;
  editMode: boolean = false;
  createMode: boolean = false;
  quotationStatusList: QuotationStatus[] = [] as Array<QuotationStatus>;
  customerOrderStatusList: CustomerOrderStatus[] = [] as Array<CustomerOrderStatus>;
  isQuotationUrl = false;
  VALIDATED_BY_CUSTOMER = VALIDATED_BY_CUSTOMER;
  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  CUSTOMER_ORDER_STATUS_BILLED = CUSTOMER_ORDER_STATUS_BILLED;
  CUSTOMER_ORDER_STATUS_ABANDONED = CUSTOMER_ORDER_STATUS_ABANDONED;

  incidentList: IncidentReport[] | undefined;
  askForNewCri: boolean = false;

  billingLabelTypeAffaire: BillingLabelType = this.constantService.getBillingLabelTypeCodeAffaire();

  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  instanceOfQuotationFn = instanceOfQuotation;
  round = Math.round;

  selectedTabIndex = 0;
  selectedTabIndexAsso = 0;

  @ViewChild(OrderingCustomerComponent) orderingCustomerComponent: OrderingCustomerComponent | undefined;
  @ViewChild(QuotationManagementComponent) quotationManagementComponent: QuotationManagementComponent | undefined;
  @ViewChildren(ProvisionItemComponent) provisionItemComponents: any;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  instanceOfCustomerOrder: boolean = false;
  isStatusOpen: boolean = true;

  updateDocumentsEvent: Subject<IQuotation> = new Subject<IQuotation>();

  @Input() idQuotation: number | undefined;
  @Input() inputProvision: Provision | undefined;
  @Input() isForIntegration: boolean = false;

  saveObservableSubscription: Subscription = new Subscription;
  customerOrderInvoices: InvoiceSearchResult[] | undefined;

  hasQuotation: boolean | undefined;

  constructor(private appService: AppService,
    private quotationService: QuotationService,
    private customerOrderService: CustomerOrderService,
    private quotationStatusService: QuotationStatusService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private validationIdQuotationService: ValidationIdQuotationService,
    private activatedRoute: ActivatedRoute,
    public chooseUserDialog: MatDialog,
    public mailLabelDialog: MatDialog,
    public abandonReasonInquiryDialog: MatDialog,
    public selectServiceTypeDialog: MatDialog,
    public addAffaireDialog: MatDialog,
    public selectAttachmentTypeDialog: MatDialog,
    public confirmationDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    public orderSimilaritiesDialog: MatDialog,
    public customerOrderWorkflowDialog: MatDialog,
    public suggestedQuotationDialog: MatDialog,
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    protected searchService: SearchService,
    private provisionService: ProvisionService,
    private orderingSearchResultService: OrderingSearchResultService,
    private invoiceSearchResultService: InvoiceSearchResultService,
    private habilitationsService: HabilitationsService,
    public associatePaymentDialog: MatDialog,
    private userPreferenceService: UserPreferenceService,
    private serviceService: ServiceService,
    private habilitationService: HabilitationsService,
    private notificationService: NotificationService,
    private incidentReportService: IncidentReportService,
    private quotationSearchResultService: QuotationSearchResultService,
    private changeDetectorRef: ChangeDetectorRef) { }

  quotationForm = this.formBuilder.group({});

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    if (!this.idQuotation)
      this.idQuotation = this.activatedRoute.snapshot.params.id;
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    this.quotationStatusService.getQuotationStatus().subscribe(response => {
      this.quotationStatusList = response.filter(t => t.code != "OPEN");;
    })

    this.customerOrderStatusService.getCustomerOrderStatus().subscribe(response => {
      this.customerOrderStatusList = response.filter(t => t.code != "OPEN");;
    })

    // Load by order
    if (this.isForIntegration && this.idQuotation || url != undefined && url != null && url[0] != undefined && url[0].path == "order") {
      this.isQuotationUrl = false;

      if (!this.isForIntegration)
        this.appService.changeHeaderTitle("Commande");
      this.instanceOfCustomerOrder = true;
      if (this.idQuotation != null && this.idQuotation != undefined) {
        this.customerOrderService.getCustomerOrder(this.idQuotation).subscribe(response => {
          this.quotation = response;
          if (instanceOfCustomerOrder(this.quotation) && !this.isForIntegration)
            this.appService.changeHeaderTitle("Commande " + this.quotation.id + " du " + formatDateFrance(this.quotation.createdDate) + " - " +
              (this.quotation.customerOrderStatus != null ? this.quotation.customerOrderStatus.label : "") + (this.quotation.isGifted ? (" - Offerte") : "") + (this.quotation.customerOrderOrigin && this.quotation.customerOrderOrigin.id == this.constantService.getCustomerOrderOriginMyJss().id ? (" - MyJSS") : ""));
          this.setOpenStatus();
          this.updateDocumentsEvent.next(this.quotation);

          this.restoreTab();
          this.restoreTabAsso();

          // In case of integration, put screen on right provision
          if (this.inputProvision) {
            this.selectedTabIndex = 1;
            if (this.quotation.assoAffaireOrders)
              for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
                if (this.quotation.assoAffaireOrders[i].id == this.inputProvision.service.assoAffaireOrder.id)
                  this.selectedTabIndexAsso = i;
              }
          }

          this.quotationSearchResultService.getQuotationsForCustomerOrder(this.quotation as CustomerOrder).subscribe(response => {
            if (response && response.length > 0)
              this.hasQuotation = true;
            else this.hasQuotation = false;
          });

          this.incidentReportService.getIncidentReportsForCustomerOrder(this.quotation.id).subscribe(response => this.incidentList = response);
        })
        this.getInvoices();
        if (instanceOfCustomerOrder(this.quotation))
          this.quotationSearchResultService.getQuotationsForCustomerOrder(this.quotation).subscribe(response => {
            if (response)
              this.hasQuotation;
            else this.hasQuotation = false;
          });

      }
      // Load by quotation
    } else if (this.idQuotation != null && this.idQuotation != undefined) {
      this.isQuotationUrl = true;
      this.appService.changeHeaderTitle("Devis");
      this.quotationService.getQuotation(this.idQuotation).subscribe(response => {
        this.quotation = response;

        this.restoreTab();
        this.restoreTabAsso();

        if (instanceOfQuotation(this.quotation))
          this.appService.changeHeaderTitle("Devis " + this.quotation.id + " du " + formatDateFrance(this.quotation.createdDate) + " - " +
            (this.quotation.quotationStatus != null ? this.quotation.quotationStatus.label : ""));
        this.setOpenStatus();
        this.updateDocumentsEvent.next(this.quotation);
      })
    } else if (this.createMode == false) {
      this.isQuotationUrl = true;
      // Blank page
      this.appService.changeHeaderTitle("Devis");
    }

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveQuotation()
        else if (this.quotation.id)
          this.editQuotation()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  canOfferCustomerOrder() {
    return this.habilitationsService.canOfferCustomerOrder();
  }

  canReinitInvoicing() {
    return this.habilitationsService.canReinitInvoicing();
  }

  setOpenStatus() {
    this.isStatusOpen = false;
    if (instanceOfCustomerOrder(this.quotation) && !this.quotation.customerOrderStatus || instanceOfQuotation(this.quotation) && !this.quotation.quotationStatus)
      this.isStatusOpen = true;
    if (this.quotation && instanceOfQuotation(this.quotation) && this.quotation.quotationStatus)
      this.isStatusOpen = true;
    if (this.quotation && instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus)
      this.isStatusOpen = this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_OPEN
        || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BEING_PROCESSED
        || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT;
  }

  isValidatedStatusForUploadFile() {
    if (this.quotation && instanceOfQuotation(this.quotation) && this.quotation.quotationStatus && this.quotation.quotationStatus.code == VALIDATED_BY_CUSTOMER)
      return false;
    else
      return this.editMode;
  }

  updateDocuments() {
    this.updateDocumentsEvent.next(this.quotation);
  }

  getInvoices() {
    this.invoiceSearchResultService.getInvoiceForCustomerOrder({ id: this.idQuotation } as CustomerOrder).subscribe(response => this.customerOrderInvoices = response);
  }

  updateAssignedToForProvision(employee: any, provision: Provision) {
    this.provisionService.updateAssignedToForProvision(provision, employee).subscribe(response => {
      provision.assignedTo = employee;
    });
  }

  saveQuotation(): boolean {
    this.setOpenStatus();
    // Can't find a way to make it work correctly ...
    replaceDocument(this.constantService.getDocumentTypeBilling(), this.quotation, this.quotationManagementComponent?.getBillingDocument()!);
    replaceDocument(this.constantService.getDocumentTypeDigital(), this.quotation, this.quotationManagementComponent?.getDigitalDocument()!);
    replaceDocument(this.constantService.getDocumentTypePaper(), this.quotation, this.quotationManagementComponent?.getPaperDocument()!);
    if (this.getFormsStatus()) {
      if (!this.instanceOfCustomerOrder) {
        this.quotationService.addOrUpdateQuotation(this.quotation).subscribe(response => {
          this.editMode = false;
          this.quotation = response;
          this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
        })
      } else {
        this.customerOrderService.addOrUpdateCustomerOrder(this.quotation).subscribe(response => {
          this.editMode = false;
          this.quotation = response;
          this.appService.openRoute(null, '/order/' + this.quotation.id, null);
        })
      }
      return true;
    }
    return false;
  }

  getFormsStatus(): boolean {

    let orderingCustomerFormStatus = this.orderingCustomerComponent?.getFormStatus();
    let quotationManagementFormStatus = this.quotationManagementComponent?.getFormStatus();

    let provisionStatus = true;
    this.provisionItemComponents.toArray().forEach((provisionComponent: { getFormStatus: () => any; }) => {
      provisionStatus = provisionStatus && provisionComponent.getFormStatus();
    });

    if (this.isStatusOpen && quotationManagementFormStatus)
      return true;

    let errorMessages: string[] = [] as Array<string>;
    if (!orderingCustomerFormStatus)
      errorMessages.push("Onglet Donneur d'ordre");
    if (!quotationManagementFormStatus)
      errorMessages.push("Onglet Eléments " + (this.instanceOfCustomerOrder ? "de la commande" : "du devis"));
    if (!provisionStatus)
      errorMessages.push("Onglet Prestations");
    if (errorMessages.length > 0) {
      let errorMessage = "Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : " + errorMessages.join(" / ");
      this.appService.displaySnackBar(errorMessage, true, 15);
      return false;
    } else {
      if (this.canCreateMultipleAffaire() == false) {
        let errorMessage = "Il est impossible de créer deux affaires car le libellé d'envoi ou les destinataires sont à l'affaire";
        this.appService.displaySnackBar(errorMessage, true, 15);
        return false;
      }
    }
    return true;
  }

  editQuotation() {
    if (instanceOfQuotation(this.quotation) && this.quotation.quotationStatus.code == VALIDATED_BY_CUSTOMER) {
      this.appService.displaySnackBar("Impossible de modifier un devis validé par le client. Il s'agit maintenant d'une commande ", true, 30);
      return;
    }
    this.editMode = true;
  }

  createQuotation() {
    this.createMode = true;
    this.editMode = true;
    this.quotation = {} as IQuotation;
    this.validationIdQuotationService.getValidationIdForQuotation().subscribe(response => {
      if (response) {
        this.quotation.validationId = response;
      }
      this.setOpenStatus();
      this.appService.changeHeaderTitle(this.instanceOfCustomerOrder ? "Nouvelle commande" : "Nouveau devis");
    });
  }

  openSearch() {
    this.searchService.openSearchOnModule(this.getEntityType());
  }

  getEntityType(): EntityType {
    return this.instanceOfCustomerOrder ? CUSTOMER_ORDER_ENTITY_TYPE : QUOTATION_ENTITY_TYPE;
  }

  getParseTypeList(): IReferential[] | undefined {
    return this.instanceOfCustomerOrder ? this.customerOrderStatusList : this.quotationStatusList;
  }

  createService(asso: AssoAffaireOrder) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus && (this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
      }
    if (asso && !asso.services)
      asso.services = [] as Array<Service>;


    if (this.hasQuotation) {
      const dialogConfirm = this.confirmationDialog.open(ConfirmDialogComponent, {
        maxWidth: "400px",
        data: {
          title: "Modification du devis de la commande",
          content: "L'ajout d'une prestation impactera le devis initial lié à cette commande. Souhaitez-vous continuer ? ",
          closeActionText: "Annuler",
          validationActionText: "Confirmer"
        }
      });

      dialogConfirm.afterClosed().subscribe(userConfirmed => {
        if (userConfirmed) {
          let dialogRef = this.selectAttachmentTypeDialog.open(SelectMultiServiceTypeDialogComponent, {
            width: '50%',
          });
          dialogRef.componentInstance.affaire = asso.affaire;

          dialogRef.afterClosed().subscribe(response => {
            if (response != null)
              asso.services.push(...response);
          });
        }
      });
    } else {
      let dialogRef = this.selectAttachmentTypeDialog.open(SelectMultiServiceTypeDialogComponent, {
        width: '50%',
      });
      dialogRef.componentInstance.affaire = asso.affaire;

      dialogRef.afterClosed().subscribe(response => {
        if (response != null)
          asso.services.push(...response);
      });
    }
  }

  createProvision(service: Service): Provision {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder()) {
      if (instanceOfCustomerOrder(this.quotation) &&
        this.quotation.customerOrderStatus &&
        (this.quotation.customerOrderStatus.code === CUSTOMER_ORDER_STATUS_TO_BILLED ||
          this.quotation.customerOrderStatus.code === CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
        return {} as Provision;
      }
    }
    let provision = {} as Provision;
    if (this.hasQuotation) {
      const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
        maxWidth: "400px",
        data: {
          title: "Modification du devis de la commande",
          content: "L'ajout d'une prestation impactera le devis initial lié à cette commande. Souhaitez-vous continuer ? ",
          closeActionText: "Annuler",
          validationActionText: "Confirmer"
        }
      });

      dialogRef.afterClosed().subscribe(userConfirmed => {
        if (userConfirmed) {
          if (service && !service.provisions) {
            service.provisions = [] as Array<Provision>;
          }
          service.provisions.push(provision);
          this.generateInvoiceItem();
        }
      });
    } else {
      if (service && !service.provisions) {
        service.provisions = [] as Array<Provision>;
      }
      service.provisions.push(provision);
      this.generateInvoiceItem();
    }
    return provision;
  }

  deleteService(asso: AssoAffaireOrder, service: Service) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus && (this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
      }

    if (this.hasQuotation) {
      const dialogConfirm = this.confirmationDialog.open(ConfirmDialogComponent, {
        maxWidth: "400px",
        data: {
          title: "Modification du devis de la commande",
          content: "La suppression d'un service impactera le devis initial lié à cette commande. Souhaitez-vous continuer ? ",
          closeActionText: "Annuler",
          validationActionText: "Confirmer"
        }
      });

      dialogConfirm.afterClosed().subscribe(userConfirmed => {
        if (userConfirmed) {
          this.deleteServiceDialog(service);
        }
      });
    } else {
      this.deleteServiceDialog(service);
    }
  }

  deleteServiceDialog(service: Service) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer le service",
        content: "Êtes-vous sûr de vouloir continuer ?",
        closeActionText: "Annuler",
        validationActionText: "Confirmer"
      }
    });

    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.serviceService.deleteService(service).subscribe(response => {
          if (!this.instanceOfCustomerOrder) {
            this.editMode = false;
            this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
          } else {
            this.editMode = false;
            this.appService.openRoute(null, '/order/' + this.quotation.id, null);
          }
        });
      }
    });
  }

  modifyService(service: Service, affaire: Affaire) {
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
        dialogRef2.componentInstance.affaire = affaire;

        dialogRef2.afterClosed().subscribe(dialogResult => {
          if (dialogResult && service && this.quotation) {
            this.serviceService.modifyServiceType(service, dialogResult).subscribe(response => {
              if (!this.instanceOfCustomerOrder) {
                this.appService.openRoute(null, '/quotation/' + this.quotation!.id, null);
              } else {
                this.appService.openRoute(null, '/order/' + this.quotation.id, null);
              }
            })
          }
        });
      }
    });
  }

  displaySnakBarLockProvision() {
    this.appService.displaySnackBar("Il n'est pas possible d'ajouter ou modifier une prestation sur une commande au statut A facturer ou Facturer. Veuillez modifier le statut de la commande.", false, 15);
  }

  addAffaire() {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus && (this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
        return;
      }

    let dialogRef = this.addAffaireDialog.open(AddAffaireDialogComponent, {
      width: '100%',
      height: '90%'
    });

    if (this.quotationManagementComponent?.getBillingDocument() && this.quotationManagementComponent?.getBillingDocument().billingLabelType && this.quotationManagementComponent?.getBillingDocument().billingLabelType.id)
      dialogRef.componentInstance.isLabelAffaire = this.quotationManagementComponent?.getBillingDocument()!.isRecipientAffaire;

    dialogRef.afterClosed().subscribe(response => {
      if (response != null) {

        if (this.quotation && this.quotation.assoAffaireOrders)
          for (let existingAsso of this.quotation.assoAffaireOrders)
            if (existingAsso.affaire.id == response.id) {
              this.appService.displaySnackBar("Cette affaire est déjà associée à la commande", true, 10);
              return;
            }

        let asso = {} as AssoAffaireOrder;
        asso.affaire = response;
        asso.services = [] as Array<Service>;

        if (!this.quotation.assoAffaireOrders)
          this.quotation.assoAffaireOrders = [] as Array<AssoAffaireOrder>;

        // Check if another quotation / affaire already exists
        let orderingSearch = {} as OrderingSearch;
        orderingSearch.customerOrders = [this.quotation.responsable as any as Tiers];
        orderingSearch.affaire = asso.affaire;
        orderingSearch.customerOrderStatus = [];
        let d = new Date();
        d.setDate(d.getDate() - 15);
        orderingSearch.startDate = d;
        if (this.customerOrderStatusList)
          for (let status of this.customerOrderStatusList)
            if ([CUSTOMER_ORDER_STATUS_OPEN, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_BEING_PROCESSED].indexOf(status.code) >= 0)
              orderingSearch.customerOrderStatus.push(status);

        this.orderingSearchResultService.getOrders(orderingSearch).subscribe(orders => {
          if (orders && orders.length > 0) {
            let dialogRef = this.orderSimilaritiesDialog.open(OrderSimilaritiesDialogComponent);
            dialogRef.componentInstance.affaire = response;
            dialogRef.componentInstance.orderingSearch = orderingSearch;

            dialogRef.afterClosed().subscribe(accept => {
              if (accept) {
                this.quotation.assoAffaireOrders.push(asso);
                this.selectedTabIndex = 1;
              }
              //After checking suggested orders, check suggested quotation with same affaire
              this.quotationService.getQuotationByAffaire(asso.affaire).subscribe(response => {
                if (response && response.length > 0)
                  this.openSuggestedQuotationDialog(asso);
              });
            });

          } else {
            this.quotation.assoAffaireOrders.push(asso);
            this.selectedTabIndex = 1;
          }
        });
      }
    })
  }

  openSuggestedQuotationDialog(asso: AssoAffaireOrder) {
    let dialogQuotation = this.suggestedQuotationDialog.open(SuggestedQuotationsDialogComponent, {
      width: '100%'
    });
    dialogQuotation.componentInstance.selectedAffaire = asso.affaire;
  }

  displayQuotationWorkflowDialog() {
    let dialogRef = this.quotationWorkflowDialog.open(WorkflowDialogComponent, {
      width: '100%',
    });
    dialogRef.componentInstance.workflowElements = this.quotationStatusList;
    for (let status of this.quotationStatusList) {
      if (status.code == QUOTATION_STATUS_OPEN)
        dialogRef.componentInstance.fixedWorkflowElement = status;
      if (status.code == QUOTATION_STATUS_ABANDONED)
        dialogRef.componentInstance.excludedWorkflowElement = status;
    }
    if (instanceOfQuotation(this.quotation))
      dialogRef.componentInstance.activeWorkflowElement = this.quotation.quotationStatus;
    dialogRef.componentInstance.title = "Workflow des devis";
  }

  displayCustomerOrderWorkflowDialog() {
    let dialogRef = this.customerOrderWorkflowDialog.open(WorkflowDialogComponent, {
      width: '100%',
    });
    dialogRef.componentInstance.workflowElements = this.customerOrderStatusList;
    for (let status of this.customerOrderStatusList) {
      if (status.code == CUSTOMER_ORDER_STATUS_OPEN)
        dialogRef.componentInstance.fixedWorkflowElement = status;
      if (status.code == CUSTOMER_ORDER_STATUS_ABANDONED)
        dialogRef.componentInstance.excludedWorkflowElement = status;
    }
    if (instanceOfCustomerOrder(this.quotation))
      dialogRef.componentInstance.activeWorkflowElement = this.quotation.customerOrderStatus;
    dialogRef.componentInstance.title = "Workflow des commandes";
  }

  deleteProvision(provision: Provision) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus && (this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
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

    if (this.hasQuotation) {
      const dialogConfirm = this.confirmationDialog.open(ConfirmDialogComponent, {
        maxWidth: "400px",
        data: {
          title: "Modification du devis de la commande",
          content: "La suppression d'une prestation impactera le devis initial lié à cette commande. Souhaitez-vous continuer ? ",
          closeActionText: "Annuler",
          validationActionText: "Confirmer"
        }
      });

      dialogConfirm.afterClosed().subscribe(userConfirmed => {
        if (userConfirmed) {
          this.deleteProvisionDialog(provision);
        }
      });
    } else {
      this.deleteProvisionDialog(provision);
    }
  }

  deleteProvisionDialog(provision: Provision) {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer la prestation",
        content: "Êtes-vous sûr de vouloir continuer ?",
        closeActionText: "Annuler",
        validationActionText: "Confirmer"
      }
    });
    dialogRef.afterClosed().subscribe(response => {
      if (response) {
        this.provisionService.deleteProvision(provision).subscribe(response => {
          if (!this.instanceOfCustomerOrder) {
            this.editMode = false;
            this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
          } else {
            this.editMode = false;
            this.appService.openRoute(null, '/order/' + this.quotation.id, null);
          }
        });
      }
    });
  }

  duplicateProvision(service: Service, provision: Provision): Provision {
    let newProvisionDuplicated = {} as Provision;
    newProvisionDuplicated.provisionFamilyType = provision.provisionFamilyType;
    newProvisionDuplicated.provisionType = provision.provisionType;
    newProvisionDuplicated.assignedTo = provision.assignedTo;
    service.provisions.push(newProvisionDuplicated);
    return newProvisionDuplicated;
  }

  changeStatus(targetStatus: IWorkflowElement<any>) {
    if (targetStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED && instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT && this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_OPEN && !this.habilitationService.isAdministrator()) {
      this.appService.displaySnackBar("Impossible, veuillez contacter un administrateur", true, 10);
      return;
    }
    this.isStatusOpen = true;
    this.editMode = true;
    setTimeout(() => {
      if (this.getFormsStatus() || targetStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED) {
        if (targetStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED) {
          this.setQuotationAbandonReasonAndChangeStatus(targetStatus);
        } else {
          this.setOpenStatus();
          this.changeQuotationStatus(targetStatus);
        }
      }
      this.editMode = false;
    }, 100);
  }

  setQuotationAbandonReasonAndChangeStatus(targetStatus: QuotationStatus) {
    const dialogRef: MatDialogRef<QuotationAbandonReasonDialog> = this.abandonReasonInquiryDialog.open(QuotationAbandonReasonDialog, {
      maxWidth: "600px",
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        this.quotation.abandonReason = result;
        if (instanceOfCustomerOrder(this.quotation))
          this.customerOrderService.addOrUpdateCustomerOrder(this.quotation).subscribe(response => {
            this.quotation = response;
            this.changeQuotationStatus(targetStatus);
          });
        else
          this.quotationService.addOrUpdateQuotation(this.quotation).subscribe(response => {
            this.quotation = response;
            this.changeQuotationStatus(targetStatus);
          });
      } else {
        this.isStatusOpen = false;
        this.editMode = false;
      }
    });
  }

  changeQuotationStatus(targetStatus: QuotationStatus) {
    if (!this.instanceOfCustomerOrder) {
      this.quotationService.updateQuotationStatus(this.quotation, targetStatus.code).subscribe(response => {
        this.quotation = response;
        this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
      });
    } else {
      let hasPayment = false;
      if ((this.quotation as CustomerOrder).payments && (this.quotation as CustomerOrder).payments.length > 0) {
        for (let payment of (this.quotation as CustomerOrder).payments)
          if (payment.isCancelled == false)
            hasPayment = true;
      }
      if (hasPayment && ((this.getRemainingToPay() < -INVOICING_PAYMENT_LIMIT_REFUND_EUROS && targetStatus.code == CUSTOMER_ORDER_STATUS_BILLED) || targetStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED)) {
        let dialogPaymentDialogRef = this.associatePaymentDialog.open(AssociatePaymentDialogComponent, {
          width: '100%'
        });

        for (let payment of (this.quotation as CustomerOrder).payments)
          if (!payment.isCancelled)
            dialogPaymentDialogRef.componentInstance.payment = payment;
        dialogPaymentDialogRef.componentInstance.doNotInitializeAsso = targetStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED;
        dialogPaymentDialogRef.componentInstance.customerOrder = this.quotation as CustomerOrder;
        if (targetStatus.code == CUSTOMER_ORDER_STATUS_BILLED)
          dialogPaymentDialogRef.componentInstance.isForQuotationBilling = true;

        dialogPaymentDialogRef.afterClosed().subscribe(response => {
          if (response)
            this.customerOrderService.updateCustomerStatus(this.quotation, targetStatus.code).subscribe(response => {
              this.quotation = response;
              this.appService.openRoute(null, '/order/' + this.quotation.id, null);
            })
          else
            this.appService.openRoute(null, '/order/' + this.quotation.id, null);
        });
      } else {
        this.customerOrderService.updateCustomerStatus(this.quotation, targetStatus.code)
          .subscribe(response => {
            this.quotation = response;
            this.appService.openRoute(null, '/order/' + this.quotation.id, null);
          });

      }
    }
  }


  changeSelectedProvisionType($event: any) {
    this.generateInvoiceItem();
  }

  invoiceItemChange($event: any) {
    this.generateInvoiceItem();
  }

  onStateChange() {
    this.appService.openRoute(null, '/order/' + this.quotation.id, null);
  }

  currentInvoiceGeneration: boolean = false;
  generateInvoiceItem() {
    if (!this.currentInvoiceGeneration && this.quotation && this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders[0]
      && this.quotation.assoAffaireOrders[0].services && this.quotation.assoAffaireOrders[0].services[0]
      && this.quotation.assoAffaireOrders[0].services[0].provisions && this.quotation.assoAffaireOrders[0].services[0].provisions[0]
      && this.quotation.assoAffaireOrders[0].services[0].provisions[0].provisionType && this.quotation.assoAffaireOrders[0].services[0].provisions[0].isRedactedByJss != null) {
      this.currentInvoiceGeneration = true;
      this.quotationService.getInvoiceItemsForQuotation(this.quotation).subscribe(response => {
        this.currentInvoiceGeneration = false;
        this.mergeInvoiceItem(this.quotation, response);
      })
    }
  }

  mergeInvoiceItem(targetQuotation: IQuotation, incomingQuotation: IQuotation) {
    if (incomingQuotation && targetQuotation && incomingQuotation.assoAffaireOrders && targetQuotation.assoAffaireOrders) {
      for (let assoIncoming of incomingQuotation.assoAffaireOrders) {
        for (let assoTarget of targetQuotation.assoAffaireOrders) {
          for (let serviceIncoming of assoIncoming.services) {
            for (let serviceTarget of assoTarget.services) {
              if (serviceIncoming.provisions && serviceTarget.provisions) {
                for (let incomingProvision of serviceIncoming.provisions) {
                  for (let targetProvision of serviceTarget.provisions) {
                    if (incomingProvision.invoiceItems && targetProvision.invoiceItems) {
                      for (let i = 0; i < incomingProvision.invoiceItems.length; i++) {
                        const incomingInvoiceItem = incomingProvision.invoiceItems[i];
                        for (let j = 0; j < targetProvision.invoiceItems.length; j++) {
                          const targetInvoiceItem = targetProvision.invoiceItems[j];
                          if (incomingInvoiceItem.id == targetInvoiceItem.id)
                            targetProvision.invoiceItems[j] = incomingProvision.invoiceItems[i];
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  getPreTaxPriceTotal(): number {
    return QuotationComponent.computePreTaxPriceTotal(this.quotation);
  }

  public static computePreTaxPriceTotal(quotation: IQuotation): number {
    let preTaxPrice = 0;
    if (quotation && quotation.assoAffaireOrders) {
      for (let asso of quotation.assoAffaireOrders) {
        for (let service of asso.services)
          if (service.provisions) {
            for (let provision of service.provisions) {
              if (provision.invoiceItems) {
                for (let invoiceItem of provision.invoiceItems) {
                  preTaxPrice += parseFloat(invoiceItem.preTaxPrice + "");
                }
              }
            }
          }
      }
    }
    return preTaxPrice;
  }


  getDiscountTotal(): number {
    return QuotationComponent.computeDiscountTotal(this.quotation);
  }

  public static computeDiscountTotal(quotation: IQuotation): number {

    let discountAmount = 0;
    if (quotation && quotation.assoAffaireOrders) {
      for (let asso of quotation.assoAffaireOrders) {
        for (let service of asso.services)
          if (service.provisions) {
            for (let provision of service.provisions) {
              if (provision.invoiceItems) {
                for (let invoiceItem of provision.invoiceItems) {
                  if (invoiceItem.discountAmount)
                    discountAmount += parseFloat(invoiceItem.discountAmount + "");
                }
              }
            }
          }
      }
    }
    return discountAmount;
  }

  getVatTotal(): number {
    return QuotationComponent.computeVatTotal(this.quotation);
  }

  public static computeVatTotal(quotation: IQuotation): number {
    let vat = 0;
    if (quotation && quotation.assoAffaireOrders) {
      for (let asso of quotation.assoAffaireOrders) {
        for (let service of asso.services)
          if (service.provisions) {
            for (let provision of service.provisions) {
              if (provision.invoiceItems) {
                for (let invoiceItem of provision.invoiceItems) {
                  if (invoiceItem.vatPrice)
                    vat += invoiceItem.vatPrice;
                }
              }
            }
          }
      }
    }
    return vat;
  }

  getApplicableVat(): VatBase[] {
    return QuotationComponent.computeApplicableVat(this.quotation);
  }

  public static computeApplicableVat(quotation: IQuotation): VatBase[] {
    let vatBases: VatBase[] = [];
    if (quotation && quotation.assoAffaireOrders) {
      for (let asso of quotation.assoAffaireOrders) {
        for (let service of asso.services)
          if (service.provisions) {
            for (let provision of service.provisions) {
              if (provision.invoiceItems) {
                for (let invoiceItem of provision.invoiceItems) {
                  if (invoiceItem.vat && invoiceItem.vatPrice) {
                    let vatFound = false;
                    for (let vatBase of vatBases) {
                      if (vatBase.label == invoiceItem.vat.label) {
                        vatFound = true;
                        vatBase.base += invoiceItem.preTaxPrice - (invoiceItem.discountAmount ? invoiceItem.discountAmount : 0);
                        vatBase.total += invoiceItem.vatPrice;
                      }
                    }
                    if (!vatFound) {
                      vatBases.push({ label: invoiceItem.vat.label, base: (invoiceItem.preTaxPrice - (invoiceItem.discountAmount ? invoiceItem.discountAmount : 0)), total: invoiceItem.vatPrice });
                    }
                  }
                }
              }
            }
          }
      }
    }
    return vatBases;
  }

  deleteAffaire(affaire: Affaire) {
    if (!this.habilitationService.canByPassProvisionLockOnBilledOrder())
      if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus && (this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
        this.displaySnakBarLockProvision();
        return;
      }

    if (this.quotation && this.quotation.assoAffaireOrders)
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
        const asso = this.quotation.assoAffaireOrders[i];
        if (asso.affaire && asso.affaire.id == affaire.id) {
          for (let service of asso.services)
            if (service.provisions) {
              for (let provision of service.provisions)
                if (provision && provision.payments) {
                  for (let payment of provision.payments)
                    if (!payment.isCancelled) {
                      this.appService.displaySnackBar("Il n'est pas possible de supprimer cette prestation : des paiements ont déjà été déclarés.", false, 15);
                      return;
                    }
                }
            }
        }
      }

    if (this.quotation && this.quotation.assoAffaireOrders)
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
        const asso = this.quotation.assoAffaireOrders[i];
        if (asso.affaire && asso.affaire.id == affaire.id) {
          this.quotation.assoAffaireOrders.splice(i, 1);
        }
      }
  }

  getPriceTotal(): number {
    return QuotationComponent.computePriceTotal(this.quotation);
  }

  public static computePriceTotal(quotation: IQuotation): number {
    return Math.round((QuotationComponent.computePreTaxPriceTotal(quotation) - QuotationComponent.computeDiscountTotal(quotation) + QuotationComponent.computeVatTotal(quotation)) * 100) / 100;
  }

  public static computePayed(quotation: CustomerOrder) {
    let total = 0;
    if (quotation && quotation.payments)
      for (let deposit of quotation.payments)
        if (!deposit.isCancelled)
          total += deposit.paymentAmount;
    return total;
  }

  getRemainingToPay() {
    if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus)
      if (this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_BILLED)
        return Math.round((QuotationComponent.computePriceTotal(this.quotation) - QuotationComponent.computePayed(this.quotation)) * 100) / 100;
      else if (this.customerOrderInvoices) {
        for (let invoice of this.customerOrderInvoices)
          if (invoice.invoiceStatusCode != this.constantService.getInvoiceStatusCancelled().code)
            return invoice.remainingToPay;
      }
    return this.getPriceTotal();
  }

  // When quotation label type is AFFAIRE, only one affaire is authorized in quotation
  canCreateMultipleAffaire(): boolean {
    let billingDocument = getDocument(this.constantService.getDocumentTypeBilling(), this.quotation);
    if (billingDocument && billingDocument.isRecipientAffaire && this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders.length > 1) {
      this.appService.displaySnackBar("Il est impossible de créer deux affaires car le libellé d'envoi ou les destinataires sont à l'affaire", true, 20);
      return false;
    }
    return true;
  }

  //Tabs management
  changeTab(event: any) {
    this.userPreferenceService.setUserTabsSelectionIndex('quotation', event.index);
    if (!this.quotation.assoAffaireOrders && event && event.tab && event.tab.textLabel == "Prestations")
      this.addAffaire();
  }

  restoreTab() {
    this.selectedTabIndex = this.userPreferenceService.getUserTabsSelectionIndex('quotation');
  }

  changeTabAsso(event: any) {
    this.userPreferenceService.setUserTabsSelectionIndex('quotation-asso', event.index);
  }

  restoreTabAsso() {
    this.selectedTabIndexAsso = this.userPreferenceService.getUserTabsSelectionIndex('quotation-asso');
  }

  generateQuotationMail() {
    this.quotationService.generateQuotationMail(this.quotation).subscribe(response => { });
  }

  reinitInvoicing() {
    if (this.quotation && this.quotation.id && this.instanceOfCustomerOrderFn(this.quotation) && this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_ABANDONED && this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_BILLED)
      this.customerOrderService.reinitInvoicing(this.quotation).subscribe(response => {
        this.appService.openRoute(null, '/order/' + this.quotation.id, null);
      })
  }

  generateCustomerOrderCreationConfirmationToCustomer() {
    this.quotationService.generateCustomerOrderCreationConfirmationToCustomer(this.quotation).subscribe(response => { });
  }

  generateInvoiceMail() {
    this.quotationService.generateInvoicetMail(this.quotation).subscribe(response => { });
  }

  generateMailingLabel() {
    const dialogRef = this.mailLabelDialog.open(PrintLabelDialogComponent, {
      maxWidth: "600px",
    });

    dialogRef.componentInstance.customerOrders.push(this.quotation.id + "");
  }

  displayAffaire(event: any, affaire: Affaire) {
    this.appService.openRoute(event, '/affaire/' + affaire.id, null);
  }

  displayProvision(event: any, asso: AssoAffaireOrder, provision: Provision) {
    this.appService.openRoute(event, '/provision/' + asso.id + "/" + provision.id, null);
  }

  getActiveWorkflowElementsForProvision(provision: Provision) {
    return ProvisionComponent.getActiveWorkflowElementsForProvision(provision);
  }

  offerCustomerOrder() {
    if (this.quotation && instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED) {
      this.customerOrderService.offerCustomerOrder(this.quotation).subscribe(response => {
        this.appService.openRoute(null, '/order/' + this.quotation.id, null);
      })
    }
  }

  public computeProvisionLabel(service: Service, provision: Provision, doNotDisplayService: boolean): string {
    let label = provision.provisionType ? (provision.provisionFamilyType.label + ' - ' + provision.provisionType.label) : '';
    if (provision.announcement && provision.announcement.department)
      label += " - Département " + provision.announcement.department.code;
    if (!doNotDisplayService)
      label = service.serviceLabelToDisplay + " - " + label;
    return label;
  }

  hasOnePm(): boolean {
    if (this.quotation && instanceOfCustomerOrder(this.quotation) && this.quotation.assoAffaireOrders)
      for (let asso of this.quotation.assoAffaireOrders)
        if (asso.services)
          for (let service of asso.services)
            if (service.missingAttachmentQueries && service.missingAttachmentQueries.length > 0)
              return true;
    return false;
  }

  hasOneCri() {
    return this.incidentList && this.incidentList.length > 0;
  }

  isAskNewCri() {
    return this.askForNewCri;
  }

  askForNewCriFn() {
    this.askForNewCri = true;
    this.selectedTabIndex = 5;
  }

  addNewNotification() {
    if (instanceOfCustomerOrder(this.quotation))
      this.appService.addPersonnalNotification(() => this.quotationNotification = undefined, this.quotationNotification, this.quotation, undefined, undefined, undefined, undefined, undefined, undefined, undefined);
    if (instanceOfQuotation(this.quotation))
      this.appService.addPersonnalNotification(() => this.quotationNotification = undefined, this.quotationNotification, undefined, undefined, undefined, undefined, this.quotation, undefined, undefined, undefined);
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
  quotationNotification: Notification[] | undefined;


  getNotificationForCustomerOrder() {
    if (this.quotationNotification == undefined) {
      this.quotationNotification = [];
      this.notificationService.getNotificationsForCustomerOrder(this.quotation.id).subscribe(response => this.quotationNotification = response);
    }
    return this.quotationNotification;
  }

  getNotificationForQuotation() {
    if (this.quotationNotification == undefined) {
      this.quotationNotification = [];
      this.notificationService.getNotificationsForQuotation(this.quotation.id).subscribe(response => this.quotationNotification = response);
    }
    return this.quotationNotification;
  }

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
