import { AfterContentChecked, ChangeDetectorRef, Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { Subject } from 'rxjs';
import { QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_BEING_PROCESSED, QUOTATION_STATUS_BILLED, QUOTATION_STATUS_CANCELLED, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, QUOTATION_STATUS_VALIDATED_BY_CUSTOMER, QUOTATION_STATUS_VALIDATED_BY_JSS, QUOTATION_STATUS_WAITING_DEPOSIT } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { BillingLabelType } from 'src/app/modules/tiers/model/BillingLabelType';
import { EntityType } from 'src/app/routing/search/EntityType';
import { CUSTOMER_ORDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { SearchService } from 'src/app/services/search.service';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { CustomerOrder } from '../../model/CustomerOrder';
import { Provision } from '../../model/Provision';
import { QuotationStatus } from '../../model/QuotationStatus';
import { AssoAffaireOrderService } from '../../services/asso.affaire.order.service';
import { CustomerOrderService } from '../../services/customer.order.service';
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
  isQuotationUrl = false;

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;

  QUOTATION_STATUS_OPEN = QUOTATION_STATUS_OPEN;
  QUOTATION_STATUS_TO_VERIFY = QUOTATION_STATUS_TO_VERIFY;
  QUOTATION_STATUS_VALIDATED_BY_JSS = QUOTATION_STATUS_VALIDATED_BY_JSS;
  QUOTATION_STATUS_SENT_TO_CUSTOMER = QUOTATION_STATUS_SENT_TO_CUSTOMER;
  QUOTATION_STATUS_VALIDATED_BY_CUSTOMER = QUOTATION_STATUS_VALIDATED_BY_CUSTOMER;
  QUOTATION_STATUS_WAITING_DEPOSIT = QUOTATION_STATUS_WAITING_DEPOSIT;
  QUOTATION_STATUS_BEING_PROCESSED = QUOTATION_STATUS_BEING_PROCESSED;
  QUOTATION_STATUS_REFUSED_BY_CUSTOMER = QUOTATION_STATUS_REFUSED_BY_CUSTOMER;
  QUOTATION_STATUS_BILLED = QUOTATION_STATUS_BILLED;
  QUOTATION_STATUS_ABANDONED = QUOTATION_STATUS_ABANDONED;
  QUOTATION_STATUS_CANCELLED = QUOTATION_STATUS_CANCELLED;
  billingLabelTypeAffaire: BillingLabelType = this.constantService.getBillingLabelTypeCodeAffaire();

  instanceOfCustomerOrderFn = instanceOfCustomerOrder;

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
    private activatedRoute: ActivatedRoute,
    public chooseUserDialog: MatDialog,
    public addAffaireDialog: MatDialog,
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

    // Load by order
    if (url != undefined && url != null && url[0] != undefined && url[0].path == "order") {
      this.isQuotationUrl = false;
      this.appService.changeHeaderTitle("Commande");
      this.instanceOfCustomerOrder = true;
      if (this.idQuotation != null && this.idQuotation != undefined) {
        this.customerOrderService.getCustomerOrder(this.idQuotation).subscribe(response => {
          this.quotation = response;
          this.appService.changeHeaderTitle("Commande " + this.quotation.id + " - " +
            (this.quotation.quotationStatus != null ? this.quotation.quotationStatus.label : ""));
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
    if (this.quotation && this.quotation.quotationStatus)
      this.isStatusOpen = this.quotation.quotationStatus.code == QUOTATION_STATUS_OPEN;
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
    this.selectedTabIndex = 1;
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

  deleteProvision(asso: AssoAffaireOrder, provision: Provision) {
    asso.provisions.splice(asso.provisions.indexOf(provision), 1);
  }

  sendQuotation() {
    //TODO
  }

  changeStatus(targetStatusCode: string) {
    let s = this.getStatusByCode(targetStatusCode);
    if (s != null) {
      this.editQuotation();
      if (!this.getFormsStatus()) {
        this.ngOnInit();
      } else {
        if (!this.instanceOfCustomerOrder) {
          this.quotationService.updateQuotationStatus(this.quotation, targetStatusCode).subscribe(response => {
            this.quotation = response;
            this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
              this.router.navigate(['/quotation/', "" + this.quotation.id])
            );
          })
        } else {
          this.customerOrderService.updateCustomerStatus(this.quotation, targetStatusCode).subscribe(response => {
            this.quotation = response;
            this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
              this.router.navigate(['/order/', "" + this.quotation.id])
            );
          })
        }
      }
      this.editMode = false;
    }
  }

  getStatusByCode(code: string): QuotationStatus | null {
    if (!this.quotationStatusList)
      return null;

    let targetStatus = this.quotationStatusList.filter(s => s.code == code);
    if (targetStatus.length != 1) {
      this.appService.displaySnackBar("Statut non trouvé ...", true, 60);
      return null;
    }
    return targetStatus[0];
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
}
