import { Component, OnInit, ViewChild, ViewChildren } from '@angular/core';
import { MatAccordion } from '@angular/material/expansion';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { QUOTATION_DOCUMENT_TYPE_CODE, QUOTATION_STATUS_ABANDONED, QUOTATION_STATUS_CANCELLED, QUOTATION_STATUS_OPEN, QUOTATION_STATUS_REFUSED_BY_CUSTOMER, QUOTATION_STATUS_SENT_TO_CUSTOMER, QUOTATION_STATUS_TO_VERIFY, QUOTATION_STATUS_VALIDATED_BY_CUSTOMER, QUOTATION_STATUS_VALIDATED_BY_JSS } from 'src/app/libs/Constants';
import { EntityType } from 'src/app/routing/search/EntityType';
import { CUSTOMER_ORDER_ENTITY_TYPE, QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { SearchService } from 'src/app/search.service';
import { Affaire } from '../../model/Affaire';
import { IQuotation } from '../../model/IQuotation';
import { NoticeTypeFamily } from '../../model/NoticeTypeFamily';
import { Provision } from '../../model/Provision';
import { QuotationStatus } from '../../model/QuotationStatus';
import { CustomerOrderService } from '../../services/customer.order.service';
import { QuotationStatusService } from '../../services/quotation-status.service';
import { QuotationService } from '../../services/quotation.service';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
import { ProvisionItemComponent } from '../provision-item/provision-item.component';
import { QuotationManagementComponent } from '../quotation-management/quotation-management.component';

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

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  CUSTOMER_ORDER_ENTITY_TYPE = CUSTOMER_ORDER_ENTITY_TYPE;
  QUOTATION_DOCUMENT_TYPE_CODE = QUOTATION_DOCUMENT_TYPE_CODE;

  QUOTATION_STATUS_OPEN = QUOTATION_STATUS_OPEN;
  QUOTATION_STATUS_TO_VERIFY = QUOTATION_STATUS_TO_VERIFY;
  QUOTATION_STATUS_VALIDATED_BY_JSS = QUOTATION_STATUS_VALIDATED_BY_JSS;
  QUOTATION_STATUS_SENT_TO_CUSTOMER = QUOTATION_STATUS_SENT_TO_CUSTOMER;
  QUOTATION_STATUS_VALIDATED_BY_CUSTOMER = QUOTATION_STATUS_VALIDATED_BY_CUSTOMER;
  QUOTATION_STATUS_REFUSED_BY_CUSTOMER = QUOTATION_STATUS_REFUSED_BY_CUSTOMER;
  QUOTATION_STATUS_ABANDONED = QUOTATION_STATUS_ABANDONED;
  QUOTATION_STATUS_CANCELLED = QUOTATION_STATUS_CANCELLED;

  selectedTabIndex = 0;

  @ViewChild('tabs', { static: false }) tabs: any;
  @ViewChild(OrderingCustomerComponent) orderingCustomerComponent: OrderingCustomerComponent | undefined;
  @ViewChild(QuotationManagementComponent) quotationManagementComponent: QuotationManagementComponent | undefined;
  @ViewChildren(ProvisionItemComponent) provisionItemComponents: any;
  @ViewChild(MatAccordion) accordion: MatAccordion | undefined;

  instanceOfCustomerOrder: boolean = false;
  isStatusOpen: boolean = true;

  filteredProvisions: Provision[] = [] as Array<Provision>;

  constructor(private appService: AppService,
    private quotationService: QuotationService,
    private customerOrderService: CustomerOrderService,
    private quotationStatusService: QuotationStatusService,
    private activatedRoute: ActivatedRoute,
    protected searchService: SearchService,
    private router: Router) { }

  ngOnInit() {
    let a = {} as NoticeTypeFamily;

    let idQuotation: number = this.activatedRoute.snapshot.params.id;
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    this.quotationStatusService.getQuotationStatus().subscribe(response => {
      this.quotationStatusList = response;
    })

    // Load by order
    if (url != undefined && url != null && url[0] != undefined && url[0].path == "order") {
      this.appService.changeHeaderTitle("Commande");
      this.instanceOfCustomerOrder = true;
      if (idQuotation != null && idQuotation != undefined) {
        this.customerOrderService.getCustomerOrder(idQuotation).subscribe(response => {
          this.quotation = response;
          this.appService.changeHeaderTitle("Commande " + this.quotation.id + " - " +
            (this.quotation.status != null ? this.quotation.status.label : ""));
          this.toggleTabs();
          this.sortProvisions();
          this.setOpenStatus();
          this.filteredProvisions = this.quotation.provisions;
        })
      }
      // Load by quotation
    } else if (idQuotation != null && idQuotation != undefined) {
      this.appService.changeHeaderTitle("Devis");
      this.quotationService.getQuotation(idQuotation).subscribe(response => {
        this.quotation = response;
        this.appService.changeHeaderTitle("Devis " + this.quotation.id + " - " +
          (this.quotation.status != null ? this.quotation.status.label : ""));
        this.toggleTabs();
        this.sortProvisions();
        this.setOpenStatus();
        this.filteredProvisions = this.quotation.provisions;
      })
    } else if (this.createMode == false) {
      // Blank page
      this.appService.changeHeaderTitle("Devis");
    }
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  setOpenStatus() {
    if (this.quotation && this.quotation.status)
      this.isStatusOpen = this.quotation.status.code == QUOTATION_STATUS_OPEN;
    this.isStatusOpen = false;
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

  applyFilter(filterValue: any) {
    if (filterValue == null || filterValue == undefined || filterValue.length == 0) {
      this.filteredProvisions = this.quotation.provisions;
      return;
    }
    this.filteredProvisions = [] as Array<Provision>;
    if (this.quotation && this.quotation.provisions) {
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
        return nameA.localeCompare(nameB);
      })
  }

  sendQuotation() {
    //TODO
  }

  changeStatus(status: string) {
    let s = this.getStatusByCode(status);
    if (s != null) {
      this.quotation.status = s;
      this.editQuotation();
      if (!this.saveQuotation())
        this.ngOnInit();
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

}
