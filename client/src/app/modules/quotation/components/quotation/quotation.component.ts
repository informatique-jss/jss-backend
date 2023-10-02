import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BEING_PROCESSED, CUSTOMER_ORDER_STATUS_BILLED, CUSTOMER_ORDER_STATUS_TO_BILLED, CUSTOMER_ORDER_STATUS_WAITING_DEPOSIT, INVOICING_PAYMENT_LIMIT_REFUND_EUROS, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, VALIDATED_BY_CUSTOMER } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { AssociatePaymentDialogComponent } from 'src/app/modules/invoicing/components/associate-payment-dialog/associate-payment-dialog.component';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { EntityType } from 'src/app/routing/search/EntityType';
import { CUSTOMER_ORDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { SearchService } from 'src/app/services/search.service';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_OPEN } from '../../../../libs/Constants';
import { replaceDocument } from '../../../../libs/DocumentHelper';
import { formatDateFrance } from '../../../../libs/FormatHelper';
import { instanceOfQuotation } from '../../../../libs/TypeHelper';
import { getCustomerOrderForIQuotation } from '../../../invoicing/components/invoice-tools';
import { InvoiceSearchResult } from '../../../invoicing/model/InvoiceSearchResult';
import { InvoiceSearchResultService } from '../../../invoicing/services/invoice.search.result.service';
import { WorkflowDialogComponent } from '../../../miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { ITiers } from '../../../tiers/model/ITiers';
import { Affaire } from '../../model/Affaire';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { OrderingSearch } from '../../model/OrderingSearch';
import { Provision } from '../../model/Provision';
import { QuotationStatus } from '../../model/QuotationStatus';
import { VatBase } from '../../model/VatBase';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { CustomerOrderStatusService } from '../../services/customer.order.status.service';
import { OrderingSearchResultService } from '../../services/ordering.search.result.service';
import { ProvisionService } from '../../services/provision.service';
import { QuotationStatusService } from '../../services/quotation-status.service';
import { QuotationService } from '../../services/quotation.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';
import { ChooseAssignedUserDialogComponent } from '../choose-assigned-user-dialog/choose-assigned-user-dialog.component';
import { OrderSimilaritiesDialogComponent } from '../order-similarities-dialog/order-similarities-dialog.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { PrintLabelDialogComponent } from '../print-label-dialog/print-label-dialog.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { ProvisionComponent } from '../provision/provision.component';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';
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

  billingLabelTypeAffaire: BillingLabelType = this.constantService.getBillingLabelTypeCodeAffaire();

  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  instanceOfQuotationFn = instanceOfQuotation;
  round = Math.round;

  selectedTabIndex = 0;
  selectedTabIndexAsso = 0;

  @ViewChild('tabs', { static: false }) tabs: any;
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

  constructor(private appService: AppService,
    private quotationService: QuotationService,
    private customerOrderService: CustomerOrderService,
    private quotationStatusService: QuotationStatusService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private activatedRoute: ActivatedRoute,
    public chooseUserDialog: MatDialog,
    public mailLabelDialog: MatDialog,
    public addAffaireDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    public orderSimilaritiesDialog: MatDialog,
    public customerOrderWorkflowDialog: MatDialog,
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    protected searchService: SearchService,
    private provisionService: ProvisionService,
    private orderingSearchResultService: OrderingSearchResultService,
    private invoiceSearchResultService: InvoiceSearchResultService,
    public associatePaymentDialog: MatDialog,
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
      this.quotationStatusList = response;
    })

    this.customerOrderStatusService.getCustomerOrderStatus().subscribe(response => {
      this.customerOrderStatusList = response;
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
              (this.quotation.customerOrderStatus != null ? this.quotation.customerOrderStatus.label : ""));
          this.toggleTabs();
          this.setOpenStatus();
          this.checkAffaireAssignation();
          this.updateDocumentsEvent.next(this.quotation);

          // In case of integration, put screen on right provision
          if (this.inputProvision) {
            this.selectedTabIndex = 1;
            if (this.quotation.assoAffaireOrders)
              for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
                if (this.quotation.assoAffaireOrders[i].id == this.inputProvision.assoAffaireOrder.id)
                  this.selectedTabIndexAsso = i;
              }
          }
        })
        this.getInvoices();
      }
      // Load by quotation
    } else if (this.idQuotation != null && this.idQuotation != undefined) {
      this.isQuotationUrl = true;
      this.appService.changeHeaderTitle("Devis");
      this.quotationService.getQuotation(this.idQuotation).subscribe(response => {
        this.quotation = response;
        if (instanceOfQuotation(this.quotation))
          this.appService.changeHeaderTitle("Devis " + this.quotation.id + " du " + formatDateFrance(this.quotation.createdDate) + " - " +
            (this.quotation.quotationStatus != null ? this.quotation.quotationStatus.label : ""));
        this.toggleTabs();
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

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  setOpenStatus() {
    this.isStatusOpen = false;
    if (instanceOfCustomerOrder(this.quotation) && !this.quotation.customerOrderStatus || instanceOfQuotation(this.quotation) && !this.quotation.quotationStatus)
      this.isStatusOpen = true;
    if (this.quotation && instanceOfQuotation(this.quotation) && this.quotation.quotationStatus)
      this.isStatusOpen = this.quotation.quotationStatus.code == QUOTATION_STATUS_OPEN;
    if (this.quotation && instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus)
      this.isStatusOpen = this.quotation.customerOrderStatus.code == QUOTATION_STATUS_OPEN;
  }

  updateDocuments() {
    this.updateDocumentsEvent.next(this.quotation);
  }

  getInvoices() {
    this.invoiceSearchResultService.getInvoiceForCustomerOrder({ id: this.idQuotation } as CustomerOrder).subscribe(response => this.customerOrderInvoices = response);
  }

  updateAssignedToForAffaire(employee: Employee, asso: AssoAffaireOrder) {
    this.assoAffaireOrderService.updateAssignedToForAsso(asso, employee).subscribe(response => {
      asso.assignedTo = employee;
    });
  }

  updateAssignedToForProvision(employee: any, provision: Provision) {
    this.provisionService.updateAssignedToForProvision(provision, employee).subscribe(response => {
      provision.assignedTo = employee;
    });
  }

  checkAffaireAssignation() {
    let userList: Employee[] = [] as Array<Employee>;
    if (this.quotation && this.instanceOfCustomerOrder && this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders.length > 0
      && this.quotation.assoAffaireOrders[0].provisions && this.quotation.assoAffaireOrders[0].provisions.length > 0)
      for (let asso of this.quotation.assoAffaireOrders)
        if (asso.affaire && asso.provisions && !asso.assignedTo) {
          let found = false;
          for (let provision of asso.provisions) {
            for (let employee of userList) {
              if (provision.assignedTo && provision.assignedTo.id == employee.id)
                found = true;
            }
            if (!found && provision.assignedTo)
              userList.push(provision.assignedTo);
          }
          if (userList.length == 0) {
            let tiers: ITiers | undefined = this.quotation.responsable ?? this.quotation.tiers ?? this.quotation.confrere;
            if (tiers) {
              if (tiers.formalisteEmployee)
                userList.push(tiers.formalisteEmployee);
              if (tiers.insertionEmployee)
                userList.push(tiers.insertionEmployee);
            }
          }
          let chooseUserDialogRef = this.chooseUserDialog.open(ChooseAssignedUserDialogComponent, {
            width: '100%'
          });
          chooseUserDialogRef.componentInstance.userList = userList;
          chooseUserDialogRef.componentInstance.text = "Veuillez choisir l'utilisateur à assigner à l'affaire " + (asso.affaire.denomination ? asso.affaire.denomination : (asso.affaire.firstname + " " + asso.affaire.lastname));
          chooseUserDialogRef.componentInstance.title = "Assigner un utilisateur";
          chooseUserDialogRef.afterClosed().subscribe(response => {
            if (response)
              this.updateAssignedToForAffaire(response, asso);
            this.customerOrderService.getCustomerOrder(this.quotation.id).subscribe(response => {
              this.quotation = response;
              this.checkAffaireAssignation();
            })
          })
          return;
        }
  }

  saveQuotation(): boolean {

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
    this.setOpenStatus();
    this.appService.changeHeaderTitle(this.instanceOfCustomerOrder ? "Nouvelle commande" : "Nouveau devis");
    this.toggleTabs();
  }

  openSearch() {
    this.searchService.openSearchOnModule(this.getEntityType());
  }

  getEntityType(): EntityType {
    return this.instanceOfCustomerOrder ? CUSTOMER_ORDER_ENTITY_TYPE : QUOTATION_ENTITY_TYPE;
  }

  createProvision(asso: AssoAffaireOrder): Provision {
    if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus && (this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
      this.displaySnakBarLockProvision();
      return {} as Provision;
    }
    if (asso && !asso.provisions)
      asso.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    asso.provisions.push(provision);
    this.generateInvoiceItem();
    return provision;
  }

  displaySnakBarLockProvision() {
    this.appService.displaySnackBar("Il n'est pas possible d'ajouter ou modifier une prestation sur une commande au statut A facturer ou Facturer. Veuillez modifier le statut de la commande.", false, 15);
  }

  addAffaire() {
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
        asso.provisions = [] as Array<Provision>;
        asso.provisions.push({} as Provision);
        if (!this.quotation.assoAffaireOrders)
          this.quotation.assoAffaireOrders = [] as Array<AssoAffaireOrder>;

        // Check if another quotation / affaire already exists
        let orderingSearch = {} as OrderingSearch;
        orderingSearch.customerOrders = [getCustomerOrderForIQuotation(this.quotation)];
        orderingSearch.affaires = [asso.affaire];
        orderingSearch.customerOrderStatus = [];
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
            });
          } else {
            this.quotation.assoAffaireOrders.push(asso);
            this.selectedTabIndex = 1;
          }
        });
      }
    })
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

  deleteProvision(asso: AssoAffaireOrder, provision: Provision) {
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

    asso.provisions.splice(asso.provisions.indexOf(provision), 1);
  }

  changeStatus(targetStatus: QuotationStatus) {
    let currentStatusOpen = this.isStatusOpen;
    this.isStatusOpen = false;
    this.editMode = true;
    setTimeout(() => {
      if (this.getFormsStatus() || targetStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED) {
        if (!this.instanceOfCustomerOrder) {
          this.quotationService.updateQuotationStatus(this.quotation, targetStatus.code).subscribe(response => {
            this.quotation = response;
            this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
          })
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
          } else
            this.customerOrderService.updateCustomerStatus(this.quotation, targetStatus.code).subscribe(response => {
              this.quotation = response;
              this.appService.openRoute(null, '/order/' + this.quotation.id, null);
            })
        }
      } else {
        this.isStatusOpen = currentStatusOpen;
      }
      this.editMode = false;
    }, 100);
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
      && this.quotation.assoAffaireOrders[0].provisions && this.quotation.assoAffaireOrders[0].provisions[0]
      && this.quotation.assoAffaireOrders[0].provisions[0].provisionType && this.quotation.assoAffaireOrders[0].provisions[0].isRedactedByJss != null) {
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
          if (assoIncoming.provisions && assoTarget.provisions)
            for (let i = 0; i < assoIncoming.provisions.length; i++) {
              let incomingProvision = assoIncoming.provisions[i];
              for (let j = 0; j < assoTarget.provisions.length; j++) {
                let targetProvision = assoTarget.provisions[j];
                if (incomingProvision.id && targetProvision.id && incomingProvision.id == targetProvision.id && assoIncoming.affaire.id == assoTarget.affaire.id)
                  targetProvision.invoiceItems = incomingProvision.invoiceItems;
                else if (i == j && incomingProvision.provisionType && targetProvision.provisionType && incomingProvision.provisionType.id == targetProvision.provisionType.id && assoIncoming.affaire.id == assoTarget.affaire.id)
                  targetProvision.invoiceItems = incomingProvision.invoiceItems;
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
        if (asso.provisions) {
          for (let provision of asso.provisions) {
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
        if (asso.provisions) {
          for (let provision of asso.provisions) {
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
        if (asso.provisions) {
          for (let provision of asso.provisions) {
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
    return QuotationComponent.computeApplicableVat(this.quotation, this.constantService.getVatDeductible());
  }

  public static computeApplicableVat(quotation: IQuotation, debourVat: Vat): VatBase[] {
    let vatBases: VatBase[] = [];
    if (quotation && quotation.assoAffaireOrders) {
      for (let asso of quotation.assoAffaireOrders) {
        if (asso.provisions) {
          for (let provision of asso.provisions) {
            if (provision.invoiceItems) {
              for (let invoiceItem of provision.invoiceItems) {
                if (invoiceItem.vat && invoiceItem.vatPrice && invoiceItem.vatPrice > 0) {
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
    if (instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus && (this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_TO_BILLED || this.quotation.customerOrderStatus.code == CUSTOMER_ORDER_STATUS_BILLED)) {
      this.displaySnakBarLockProvision();
      return;
    }

    if (this.quotation && this.quotation.assoAffaireOrders)
      for (let i = 0; i < this.quotation.assoAffaireOrders.length; i++) {
        const asso = this.quotation.assoAffaireOrders[i];
        if (asso.affaire && asso.affaire.id == affaire.id) {
          if (asso.provisions) {
            for (let provision of asso.provisions)
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

  changeTab(event: any) {
    if (!this.quotation.assoAffaireOrders && event && event.tab && event.tab.textLabel == "Prestations")
      this.addAffaire();
  }

  generateQuotationMail() {
    this.quotationService.generateQuotationMail(this.quotation).subscribe(response => { });
  }

  generateCustomerOrderCreationConfirmationToCustomer() {
    this.quotationService.generateCustomerOrderCreationConfirmationToCustomer(this.quotation).subscribe(response => { });
  }

  generateCustomerOrderDepositConfirmationToCustomer() {
    this.quotationService.generateCustomerOrderDepositConfirmationToCustomer(this.quotation).subscribe(response => { });
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


  getProvisionLabel(provision: Provision): string {
    return QuotationComponent.computeProvisionLabel(provision);
  }

  public static computeProvisionLabel(provision: Provision): string {
    let label = provision.provisionType ? (provision.provisionFamilyType.label + ' - ' + provision.provisionType.label) : '';
    if (provision.announcement && provision.announcement.department)
      label += " - Département " + provision.announcement.department.code;
    return label;
  }

}
