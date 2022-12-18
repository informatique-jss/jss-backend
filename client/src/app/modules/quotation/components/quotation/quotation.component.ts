import { AfterContentChecked, ChangeDetectorRef, Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Subject } from 'rxjs';
import { CUSTOMER_ORDER_STATUS_BILLED, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN, VALIDATED_BY_CUSTOMER } from 'src/app/libs/Constants';
import { getDocument } from 'src/app/libs/DocumentHelper';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { getAmountRemaining } from 'src/app/modules/invoicing/components/invoice-tools';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { EntityType } from 'src/app/routing/search/EntityType';
import { CUSTOMER_ORDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { SearchService } from 'src/app/services/search.service';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_OPEN } from '../../../../libs/Constants';
import { replaceDocument } from '../../../../libs/DocumentHelper';
import { instanceOfQuotation } from '../../../../libs/TypeHelper';
import { AssociateDepositDialogComponent } from '../../../invoicing/components/associate-deposit-dialog/associate-deposit-dialog.component';
import { WorkflowDialogComponent } from '../../../miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { Affaire } from '../../model/Affaire';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { Provision } from '../../model/Provision';
import { QuotationStatus } from '../../model/QuotationStatus';
import { VatBase } from '../../model/VatBase';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { CustomerOrderStatusService } from '../../services/customer.order.status.service';
import { MailComputeResultService } from '../../services/mail.compute.result.service';
import { ProvisionService } from '../../services/provision.service';
import { QuotationStatusService } from '../../services/quotation-status.service';
import { QuotationService } from '../../services/quotation.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';
import { ChooseAssignedUserDialogComponent } from '../choose-assigned-user-dialog/choose-assigned-user-dialog.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
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

  billingLabelTypeAffaire: BillingLabelType = this.constantService.getBillingLabelTypeCodeAffaire();

  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  instanceOfQuotationFn = instanceOfQuotation;
  round = Math.round;

  selectedTabIndex = 0;

  @ViewChild('tabs', { static: false }) tabs: any;
  @ViewChild(OrderingCustomerComponent) orderingCustomerComponent: OrderingCustomerComponent | undefined;
  @ViewChild(QuotationManagementComponent) quotationManagementComponent: QuotationManagementComponent | undefined;
  @ViewChildren(ProvisionItemComponent) provisionItemComponents: any;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  instanceOfCustomerOrder: boolean = false;
  isStatusOpen: boolean = true;

  updateDocumentsEvent: Subject<void> = new Subject<void>();

  idQuotation: number | undefined;

  constructor(private appService: AppService,
    private quotationService: QuotationService,
    private customerOrderService: CustomerOrderService,
    private quotationStatusService: QuotationStatusService,
    private customerOrderStatusService: CustomerOrderStatusService,
    private activatedRoute: ActivatedRoute,
    public chooseUserDialog: MatDialog,
    public addAffaireDialog: MatDialog,
    public quotationWorkflowDialog: MatDialog,
    public customerOrderWorkflowDialog: MatDialog,
    private formBuilder: FormBuilder,
    private constantService: ConstantService,
    private assoAffaireOrderService: AssoAffaireOrderService,
    private mailComputeResultService: MailComputeResultService,
    protected searchService: SearchService,
    public associateDepositDialog: MatDialog,
    private provisionService: ProvisionService,
    private changeDetectorRef: ChangeDetectorRef) { }

  quotationForm = this.formBuilder.group({});

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.idQuotation = this.activatedRoute.snapshot.params.id;
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    this.quotationStatusService.getQuotationStatus().subscribe(response => {
      this.quotationStatusList = response;
    })

    this.customerOrderStatusService.getCustomerOrderStatus().subscribe(response => {
      this.customerOrderStatusList = response;
    })

    // Load by order
    if (url != undefined && url != null && url[0] != undefined && url[0].path == "order") {
      this.isQuotationUrl = false;
      this.appService.changeHeaderTitle("Commande");
      this.instanceOfCustomerOrder = true;
      if (this.idQuotation != null && this.idQuotation != undefined) {
        this.customerOrderService.getCustomerOrder(this.idQuotation).subscribe(response => {
          this.quotation = response;
          if (instanceOfCustomerOrder(this.quotation))
            this.appService.changeHeaderTitle("Commande " + this.quotation.id + " - " +
              (this.quotation.customerOrderStatus != null ? this.quotation.customerOrderStatus.label : ""));
          this.toggleTabs();
          this.setOpenStatus();
          this.checkAffaireAssignation();
        })
      }
      // Load by quotation
    } else if (this.idQuotation != null && this.idQuotation != undefined) {
      this.isQuotationUrl = true;
      this.appService.changeHeaderTitle("Devis");
      this.quotationService.getQuotation(this.idQuotation).subscribe(response => {
        this.quotation = response;
        if (instanceOfQuotation(this.quotation))
          this.appService.changeHeaderTitle("Devis " + this.quotation.id + " - " +
            (this.quotation.quotationStatus != null ? this.quotation.quotationStatus.label : ""));
        this.toggleTabs();
        this.setOpenStatus();
      })
    } else if (this.createMode == false) {
      this.isQuotationUrl = true;
      // Blank page
      this.appService.changeHeaderTitle("Devis");
    }
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
    this.updateDocumentsEvent.next();
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
    if (this.quotation && this.instanceOfCustomerOrder && this.quotation.assoAffaireOrders)
      for (let asso of this.quotation.assoAffaireOrders)
        if (asso.affaire && asso.provisions && !asso.assignedTo) {
          let found = false;
          for (let provision of asso.provisions) {
            for (let employee of userList) {
              if (provision.assignedTo && provision.assignedTo.id == employee.id)
                found = true;
            }
            if (!found)
              userList.push(provision.assignedTo);
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
    if (this.getFormsStatus()) {
      this.mailComputeResultService.getMailComputeResultForBilling(this.quotation).subscribe(response => {
        if (!response || !response.recipientsMailTo || response.recipientsMailTo.length == 0) {
          this.appService.displaySnackBar("Aucune adresse mail d'envoi trouvée !", true, 15);
          return false;
        }
        if (!this.instanceOfCustomerOrder) {
          this.quotationService.addOrUpdateQuotation(this.quotation).subscribe(response => {
            this.quotation = response;
            this.editMode = false;
            this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
          })
        } else {
          this.customerOrderService.addOrUpdateCustomerOrder(this.quotation).subscribe(response => {
            this.quotation = response;
            this.editMode = false;
            this.appService.openRoute(null, '/order/' + this.quotation.id, null);
          })
        }
        return true;
      });
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
      this.appService.displaySnackBar("Impossible de modifier un devis validé par le client. Il s'agit maintenant de la commande ", true, 30);
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
    if (asso && !asso.provisions)
      asso.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    asso.provisions.push(provision);
    this.generateInvoiceItem();
    return provision;
  }

  addAffaire() {
    let dialogRef = this.addAffaireDialog.open(AddAffaireDialogComponent, {
      width: '100%'
    });
    dialogRef.componentInstance.isLabelAffaire = this.quotationManagementComponent?.getBillingDocument()!.billingLabelType.id == this.constantService.getBillingLabelTypeCodeAffaire().id;
    dialogRef.afterClosed().subscribe(response => {
      if (response != null) {
        let asso = {} as AssoAffaireOrder;
        asso.affaire = response;
        asso.provisions = [] as Array<Provision>;
        asso.provisions.push({} as Provision);
        if (!this.quotation.assoAffaireOrders)
          this.quotation.assoAffaireOrders = [] as Array<AssoAffaireOrder>;
        this.quotation.assoAffaireOrders.push(asso);
        this.selectedTabIndex = 1;
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
    asso.provisions.splice(asso.provisions.indexOf(provision), 1);
  }

  changeStatus(targetStatus: QuotationStatus) {
    this.editMode = true;
    setTimeout(() => {
      if (this.getFormsStatus()) {
        if (!this.instanceOfCustomerOrder) {
          this.quotationService.updateQuotationStatus(this.quotation, targetStatus.code).subscribe(response => {
            this.quotation = response;
            this.appService.openRoute(null, '/quotation/' + this.quotation.id, null);
          })
        } else {
          if (this.getRemainingToPay() < 0 && targetStatus.code == CUSTOMER_ORDER_STATUS_BILLED || targetStatus.code == CUSTOMER_ORDER_STATUS_ABANDONED) {
            let dialogDepositDialogRef = this.associateDepositDialog.open(AssociateDepositDialogComponent, {
              width: '100%'
            });
            dialogDepositDialogRef.componentInstance.deposit = (this.quotation as CustomerOrder).deposits[0];
            dialogDepositDialogRef.componentInstance.customerOrder = (this.quotation as CustomerOrder);
            dialogDepositDialogRef.afterClosed().subscribe(response => {
              this.appService.openRoute(null, '/order/' + this.quotation.id, null);
            });
          } else
            this.customerOrderService.updateCustomerStatus(this.quotation, targetStatus.code).subscribe(response => {
              this.quotation = response;
              this.appService.openRoute(null, '/order/' + this.quotation.id, null);
            })
        }
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
                if (incomingProvision.id && targetProvision.id && incomingProvision.id == targetProvision.id)
                  targetProvision.invoiceItems = incomingProvision.invoiceItems;
                else if (i == j && incomingProvision.provisionType && targetProvision.provisionType && incomingProvision.provisionType.id == targetProvision.provisionType.id)
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
    return QuotationComponent.computeApplicableVat(this.quotation);
  }

  public static computeApplicableVat(quotation: IQuotation): VatBase[] {
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

  getPriceTotal(): number {
    return QuotationComponent.computePriceTotal(this.quotation);
  }

  public static computePriceTotal(quotation: IQuotation): number {
    return Math.round((QuotationComponent.computePreTaxPriceTotal(quotation) - QuotationComponent.computeDiscountTotal(quotation) + QuotationComponent.computeVatTotal(quotation)) * 100) / 100;
  }

  public static computePayed(quotation: CustomerOrder) {
    let total = 0;
    if (quotation && quotation.deposits)
      for (let deposit of quotation.deposits)
        total += deposit.depositAmount;
    return total;
  }

  getRemainingToPay() {
    if (instanceOfCustomerOrder(this.quotation))
      if (this.quotation.customerOrderStatus.code != CUSTOMER_ORDER_STATUS_BILLED)
        return Math.round((QuotationComponent.computePriceTotal(this.quotation) - QuotationComponent.computePayed(this.quotation)) * 100) / 100;
      else {
        for (let invoice of this.quotation.invoices)
          if (invoice.invoiceStatus.code != this.constantService.getInvoiceStatusCancelled().code)
            return getAmountRemaining(invoice);
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

  generateInvoicetMail() {
    this.quotationService.generateInvoicetMail(this.quotation).subscribe(response => { });
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
}
