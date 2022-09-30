import { Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { Subject } from 'rxjs';
import { QUOTATION_DOCUMENT_TYPE_CODE, QUOTATION_LABEL_TYPE_AFFAIRE_CODE, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_BEING_PROCESSED, QUOTATION_STATUS_BILLED, QUOTATION_STATUS_CANCELLED, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, QUOTATION_STATUS_VALIDATED_BY_CUSTOMER, QUOTATION_STATUS_VALIDATED_BY_JSS, QUOTATION_STATUS_WAITING_DEPOSIT } from 'src/app/libs/Constants';
import { Vat } from 'src/app/modules/miscellaneous/model/Vat';
import { EntityType } from 'src/app/routing/search/EntityType';
import { CUSTOMER_ORDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { SearchService } from 'src/app/services/search.service';
import { Affaire } from '../../model/Affaire';
import { CustomerOrder } from '../../model/CustomerOrder';
import { NoticeTypeFamily } from '../../model/NoticeTypeFamily';
import { Provision } from '../../model/Provision';
import { QuotationStatus } from '../../model/QuotationStatus';
import { CustomerOrderService } from '../../services/customer.order.service';
import { QuotationStatusService } from '../../services/quotation-status.service';
import { QuotationService } from '../../services/quotation.service';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';
import { IQuotation } from './../../model/IQuotation';

@Component({
  selector: 'quotation',
  templateUrl: './quotation.component.html',
  styleUrls: ['./quotation.component.css']
})
export class QuotationComponent implements OnInit {
  quotation: IQuotation = {} as IQuotation;
  editMode: boolean = false;
  createMode: boolean = false;
  quotationStatusList: QuotationStatus[] = [] as Array<QuotationStatus>;
  isQuotationUrl = false;

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  QUOTATION_DOCUMENT_TYPE_CODE = QUOTATION_DOCUMENT_TYPE_CODE;

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
  QUOTATION_LABEL_TYPE_AFFAIRE_CODE = QUOTATION_LABEL_TYPE_AFFAIRE_CODE;

  selectedTabIndex = 0;

  @ViewChild('tabs', { static: false }) tabs: any;
  @ViewChild(OrderingCustomerComponent) orderingCustomerComponent: OrderingCustomerComponent | undefined;
  @ViewChild(QuotationManagementComponent) quotationManagementComponent: QuotationManagementComponent | undefined;
  @ViewChildren(ProvisionItemComponent) provisionItemComponents: any;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  instanceOfCustomerOrder: boolean = false;
  isStatusOpen: boolean = true;

  filteredProvisions: Provision[] = [] as Array<Provision>;

  updateDocumentsEvent: Subject<void> = new Subject<void>();

  idQuotation: number | undefined;

  constructor(private appService: AppService,
    private quotationService: QuotationService,
    private customerOrderService: CustomerOrderService,
    private quotationStatusService: QuotationStatusService,
    private activatedRoute: ActivatedRoute,
    protected searchService: SearchService,
    private router: Router) { }

  ngOnInit() {
    let a = {} as NoticeTypeFamily;

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
          this.sortProvisions();
          this.setOpenStatus();
          this.filteredProvisions = this.quotation.provisions;
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
        this.sortProvisions();
        this.setOpenStatus();
        this.filteredProvisions = this.quotation.provisions;
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
    this.applyFilter(null);
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

  applyFilter(filterValue: any) {
    this.filteredProvisions = [] as Array<Provision>;
    if (this.quotation && this.quotation.provisions) {
      if (filterValue == null || filterValue == undefined || filterValue.length == 0) {
        this.filteredProvisions = this.quotation.provisions;
        return;
      }
      this.quotation.provisions.forEach(provision => {
        const dataStr = JSON.stringify(provision).toLowerCase();
        if (dataStr.indexOf(filterValue.value.toLowerCase()) >= 0)
          this.filteredProvisions.push(provision);
      })
    }
  }

  createProvision(): Provision {
    if (this.quotation && !this.quotation.provisions)
      this.quotation.provisions = [] as Array<Provision>;
    let provision = {} as Provision;
    provision.affaire = {} as Affaire;
    this.quotation.provisions.push(provision);
    this.sortProvisions();
    this.generateInvoiceItem();
    this.applyFilter(null);
    return provision;
  }

  createProvisionForAffaire(affaire: Affaire) {
    let provision: Provision = this.createProvision();
    provision.affaire = affaire;
    this.sortProvisions();
    this.applyFilter(null);
  }

  deleteProvision(index: number) {
    if (this.filteredProvisions && this.quotation && this.quotation.provisions) {
      for (let i = 0; i < this.quotation.provisions.length; i++) {
        const provision = this.quotation.provisions[i];
        if (this.sameProvision(provision, this.filteredProvisions[index]))
          this.quotation.provisions.splice(i, 1);
      }
    }
    this.applyFilter(null);
  }

  validateProvision(index: number) {
    if (this.editMode) {
      if (this.filteredProvisions && this.quotation && this.quotation.provisions) {
        for (let i = 0; i < this.quotation.provisions.length; i++) {
          const provision = this.quotation.provisions[i];
          if (this.sameProvision(provision, this.filteredProvisions[index]))
            this.quotation.provisions[i].isValidated = true;
        }
      }
      this.applyFilter(null);
    }
  }

  invalidateProvision(index: number) {
    if (this.editMode) {
      if (this.filteredProvisions && this.quotation && this.quotation.provisions) {
        for (let i = 0; i < this.quotation.provisions.length; i++) {
          const provision = this.quotation.provisions[i];
          if (this.sameProvision(provision, this.filteredProvisions[index]))
            this.quotation.provisions[i].isValidated = false;
        }
      }
      this.applyFilter(null);
    }
  }

  sameProvision(p1: Provision, p2: Provision): boolean {
    return JSON.stringify(p1).toLowerCase() == JSON.stringify(p2).toLowerCase();
  }

  sortProvisions() {
    if (this.quotation && this.quotation.provisions)
      this.quotation.provisions.sort((a: Provision, b: Provision) => {
        if (!a && b)
          return -1;
        if (a && !b)
          return 1;
        if (!a && !b)
          return 0;
        if (!a.id && b.id)
          return -1;
        if (a.id && !b.id)
          return 1;
        if (!a.affaire && b.affaire)
          return -1;
        if (a.affaire && !b.affaire)
          return 1;
        if (!a.affaire && !b.affaire)
          return 0;

        let nameA = "";
        let nameB = "";
        if (a.affaire.isIndividual) {
          nameA = (a.affaire.firstname != null ? a.affaire.firstname : "") + (a.affaire.lastname != null ? a.affaire.lastname : "");
        } else {
          nameA = a.affaire.denomination;
        }
        if (b.affaire.isIndividual) {
          nameB = (b.affaire.firstname != null ? b.affaire.firstname : "") + (b.affaire.lastname != null ? b.affaire.lastname : "");
        } else {
          nameB = b.affaire.denomination;
        }
        if (nameA == null)
          nameA = "";
        if (nameB == null)
          nameB = "";
        return nameA.localeCompare(nameB);
      })
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
    this.quotationService.getInvoiceItemsForQuotation(this.quotation).subscribe(response => {
      this.mergeInvoiceItem(this.quotation, response);
    })
  }

  mergeInvoiceItem(targetQuotation: IQuotation, incomingQuotation: IQuotation) {
    if (incomingQuotation && targetQuotation && incomingQuotation.provisions && targetQuotation.provisions) {
      for (let incomingProvision of incomingQuotation.provisions) {
        for (let targetProvision of targetQuotation.provisions) {
          targetProvision.invoiceItems = incomingProvision.invoiceItems;
        }
      }
    }
  }

  getPreTaxPriceTotal(): number {
    return QuotationComponent.computePreTaxPriceTotal(this.quotation);
  }

  public static computePreTaxPriceTotal(quotation: IQuotation): number {
    let preTaxPrice = 0;
    if (quotation && quotation.provisions) {
      for (let provision of quotation.provisions) {
        if (provision.invoiceItems) {
          for (let invoiceItem of provision.invoiceItems) {
            preTaxPrice += parseFloat(invoiceItem.preTaxPrice + "");
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
    if (quotation && quotation.provisions) {
      for (let provision of quotation.provisions) {
        if (provision.invoiceItems) {
          for (let invoiceItem of provision.invoiceItems) {
            discountAmount += parseFloat(invoiceItem.discountAmount + "");
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
    if (quotation && quotation.provisions) {
      for (let provision of quotation.provisions) {
        if (provision.invoiceItems) {
          for (let invoiceItem of provision.invoiceItems) {
            vat += invoiceItem.vatPrice;
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
    if (quotation && quotation.provisions) {
      for (let provision of quotation.provisions) {
        if (provision.invoiceItems) {
          for (let invoiceItem of provision.invoiceItems) {
            if (invoiceItem.vat)
              return invoiceItem.vat;
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

  // When quotation label type is AFFAIRE, only one affaire is authorized in quotation
  canCreateMultipleAffaire(): boolean {
    if (this.quotation && this.quotation.quotationLabelType && this.quotation.provisions && this.quotation.provisions.length > 0)
      if (this.quotation.quotationLabelType.code == QUOTATION_LABEL_TYPE_AFFAIRE_CODE) {
        let affaireFound = undefined;
        for (let provision of this.quotation.provisions) {
          if (affaireFound != undefined && affaireFound != provision.affaire.id)
            return false;
          affaireFound = provision.affaire.id;
        }
      }
    return true;
  }
}
