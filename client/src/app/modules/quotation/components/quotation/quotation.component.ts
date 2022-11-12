import { AfterContentChecked, ChangeDetectorRef, Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { Subject } from 'rxjs';
import { QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_OPEN } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { EntityType } from 'src/app/routing/search/EntityType';
import { CUSTOMER_ORDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { SearchService } from 'src/app/services/search.service';
import { CUSTOMER_ORDER_STATUS_ABANDONED, CUSTOMER_ORDER_STATUS_OPEN } from '../../../../libs/Constants';
import { instanceOfQuotation } from '../../../../libs/TypeHelper';
import { WorkflowDialogComponent } from '../../../miscellaneous/components/workflow-dialog/workflow-dialog.component';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { CustomerOrderStatus } from '../../model/CustomerOrderStatus';
import { Provision } from '../../model/Provision';
import { QuotationStatus } from '../../model/QuotationStatus';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { CustomerOrderService } from '../../services/customer.order.service';
import { CustomerOrderStatusService } from '../../services/customer.order.status.service';
import { QuotationStatusService } from '../../services/quotation-status.service';
import { QuotationService } from '../../services/quotation.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';
import { ChooseAssignedUserDialogComponent } from '../choose-assigned-user-dialog/choose-assigned-user-dialog.component';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
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

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;

  billingLabelTypeAffaire: BillingLabelType = this.constantService.getBillingLabelTypeCodeAffaire();

  instanceOfCustomerOrderFn = instanceOfCustomerOrder;
  instanceOfQuotationFn = instanceOfQuotation;

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
    protected searchService: SearchService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router) { }

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
    if (this.quotation && instanceOfQuotation(this.quotation) && this.quotation.quotationStatus)
      this.isStatusOpen = this.quotation.quotationStatus.code == QUOTATION_STATUS_OPEN;
    if (this.quotation && instanceOfCustomerOrder(this.quotation) && this.quotation.customerOrderStatus)
      this.isStatusOpen = this.quotation.customerOrderStatus.code == QUOTATION_STATUS_OPEN;
    this.isStatusOpen = false;
  }

  updateDocuments() {
    this.updateDocumentsEvent.next();
  }

  updateAssignedToForAffaire(employee: Employee, asso: AssoAffaireOrder) {
    this.assoAffaireOrderService.updateAssignedToForAsso(asso, employee).subscribe(response => {
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
    if (this.getFormsStatus()) {
      if (!this.instanceOfCustomerOrder) {
        this.quotationService.addOrUpdateQuotation(this.quotation).subscribe(response => {
          this.quotation = response;
          this.editMode = false;
          this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
            this.router.navigate(['/quotation/', "" + this.quotation.id])
          );
        })
      } else {
        this.customerOrderService.addOrUpdateCustomerOrder(this.quotation).subscribe(response => {
          this.quotation = response;
          this.editMode = false;
          this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
            this.router.navigate(['/order/', "" + this.quotation.id])
          );
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

    let errorMessages: string[] = [] as Array<string>;
    if (!orderingCustomerFormStatus)
      errorMessages.push("Onglet Donneur d'ordre");
    if (!quotationManagementFormStatus)
      errorMessages.push("Onglet Eléments du devis");
    if (!provisionStatus)
      errorMessages.push("Onglet Prestations");
    if (errorMessages.length > 0) {
      let errorMessage = "Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : " + errorMessages.join(" / ");
      this.appService.displaySnackBar(errorMessage, true, 60);
      return false;
    } else {
      if (this.canCreateMultipleAffaire() == false) {
        let errorMessage = "Il n'est pas possible d'avoir plusieurs affaires si le libellé d'envoi est à faire à l'affaire";
        this.appService.displaySnackBar(errorMessage, true, 60);
        return false;
      }
    }
    return true;
  }

  editQuotation() {
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

  sendQuotation() {
    //TODO
  }

  changeStatus(targetStatus: QuotationStatus) {
    this.editQuotation();
    if (this.getFormsStatus()) {
      if (!this.instanceOfCustomerOrder) {
        this.quotationService.updateQuotationStatus(this.quotation, targetStatus.code).subscribe(response => {
          this.quotation = response;
          this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
            this.router.navigate(['/quotation/', "" + this.quotation.id])
          );
        })
      } else {
        this.customerOrderService.updateCustomerStatus(this.quotation, targetStatus.code).subscribe(response => {
          this.quotation = response;
          this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
            this.router.navigate(['/order/', "" + this.quotation.id])
          );
        })
      }
    }
    this.editMode = false;
  }

  changeSelectedProvisionType($event: any) {
    this.generateInvoiceItem();
  }

  invoiceItemChange($event: any) {
    this.generateInvoiceItem();
  }

  generateInvoiceItem() {
    if (this.quotation && this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders[0] && this.quotation.assoAffaireOrders[0].provisions && this.quotation.assoAffaireOrders[0].provisions[0] && this.quotation.assoAffaireOrders[0].provisions[0].provisionType)
      this.quotationService.getInvoiceItemsForQuotation(this.quotation).subscribe(response => {
        this.mergeInvoiceItem(this.quotation, response);
      })
  }

  mergeInvoiceItem(targetQuotation: IQuotation, incomingQuotation: IQuotation) {
    if (incomingQuotation && targetQuotation && incomingQuotation.assoAffaireOrders && targetQuotation.assoAffaireOrders) {
      for (let assoIncoming of incomingQuotation.assoAffaireOrders) {
        for (let assoTarget of targetQuotation.assoAffaireOrders) {
          if (assoIncoming.provisions && assoTarget.provisions)
            for (let incomingProvision of assoIncoming.provisions) {
              for (let targetProvision of assoTarget.provisions) {
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

  getApplicableVat(): Vat | undefined {
    return QuotationComponent.computeApplicableVat(this.quotation);
  }

  public static computeApplicableVat(quotation: IQuotation): Vat | undefined {
    if (quotation && quotation.assoAffaireOrders) {
      for (let asso of quotation.assoAffaireOrders) {
        if (asso.provisions) {
          for (let provision of asso.provisions) {
            if (provision.invoiceItems) {
              for (let invoiceItem of provision.invoiceItems) {
                if (invoiceItem.vat)
                  return invoiceItem.vat;
              }
            }
          }
        }
      }
    }
    return undefined;
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
      return Math.round((QuotationComponent.computePriceTotal(this.quotation) - QuotationComponent.computePayed(this.quotation)) * 100) / 100;
    return this.getPriceTotal();
  }

  // When quotation label type is AFFAIRE, only one affaire is authorized in quotation
  canCreateMultipleAffaire(): boolean {
    if (this.quotation && this.quotation.labelType && this.quotation.assoAffaireOrders && this.quotation.assoAffaireOrders.length > 0)
      if (this.quotation.labelType.id == this.billingLabelTypeAffaire.id) {
        if (this.quotation.assoAffaireOrders.length > 1) {
          this.appService.displaySnackBar("Il est impossible de créer deux affaires sur une commande facturée à l'affaire", true, 20);
          return false;
        }
      }
    return true;
  }

  changeTab(event: any) {
    if (!this.quotation.assoAffaireOrders && event && event.tab && event.tab.textLabel == "Prestations")
      this.addAffaire();
  }
}
