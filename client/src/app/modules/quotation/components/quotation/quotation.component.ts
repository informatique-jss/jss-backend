import { Component, OnInit, ViewChild } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ActivatedRoute, Router, UrlSegment } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { QUOTATION_DOCUMENT_TYPE_CODE } from 'src/app/libs/Constants';
import { instanceOfCustomerOrder, instanceOfQuotation } from 'src/app/libs/TypeHelper';
import { ProvisionComponent } from 'src/app/modules/quotation/components/provision/provision.component';
import { QUOTATION_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { SearchService } from 'src/app/search.service';
import { IQuotation } from '../../model/IQuotation';
import { QuotationService } from '../../services/quotation.service';
import { OrderingCustomerComponent } from '../ordering-customer/ordering-customer.component';
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

  QUOTATION_ENTITY_TYPE = QUOTATION_ENTITY_TYPE;
  QUOTATION_DOCUMENT_TYPE_CODE = QUOTATION_DOCUMENT_TYPE_CODE;

  selectedTabIndex = 0;

  @ViewChild('tabs', { static: false }) tabs: any;

  @ViewChild(OrderingCustomerComponent) orderingCustomerComponent: OrderingCustomerComponent | undefined;
  @ViewChild(ProvisionComponent) provisionComponent: ProvisionComponent | undefined;
  @ViewChild(QuotationManagementComponent) quotationManagementComponent: QuotationManagementComponent | undefined;

  constructor(private appService: AppService,
    private quotationService: QuotationService,
    private snackBar: MatSnackBar,
    private activatedRoute: ActivatedRoute,
    protected searchService: SearchService,
    private router: Router) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Devis");

    let idQuotation: number = this.activatedRoute.snapshot.params.id;
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    // Load by order
    if (url != undefined && url != null && url[0] != undefined && url[1] != undefined && url[0].path == "order") {
      //TODO : load by order
      this.appService.changeHeaderTitle("Commande " + this.quotation.id);
      // Load by quotation
    } else if (idQuotation != null && idQuotation != undefined) {
      this.quotationService.getQuotation(idQuotation).subscribe(response => {
        this.quotation = response;
        this.appService.changeHeaderTitle("Devis " + this.quotation.id + " - " +
          (this.quotation.quotationStatus != null ? this.quotation.quotationStatus.label : ""));
        this.toggleTabs();
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

  saveQuotation() {
    if (this.getFormsStatus())
      this.quotationService.addOrUpdateQuotation(this.quotation).subscribe(response => {
        this.quotation = response;
        this.editMode = false;
        this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
          this.router.navigate(['/quotation/', "" + this.quotation.id])
        );
      })
  }

  getFormsStatus(): boolean {
    let orderingCustomerFormStatus = this.orderingCustomerComponent?.getFormStatus();
    let quotationManagementFormStatus = this.quotationManagementComponent?.getFormStatus();
    let provisionFormStatus = this.provisionComponent?.getFormStatus();
    let errorMessages: string[] = [] as Array<string>;
    if (!orderingCustomerFormStatus)
      errorMessages.push("Onglet Donneur d'ordre");
    if (!quotationManagementFormStatus)
      errorMessages.push("Onglet Gestion du devis");
    if (!provisionFormStatus)
      errorMessages.push("Onglet Prestations");
    if (errorMessages.length > 0) {
      let errorMessage = "Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : " + errorMessages.join(" / ");
      let sb = this.snackBar.open(errorMessage, 'Fermer', {
        duration: 60 * 1000, panelClass: ["red-snackbar"]
      });
      sb.onAction().subscribe(() => {
        sb.dismiss();
      });
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
    this.appService.changeHeaderTitle("Nouveau devis");
    this.toggleTabs();
  }

  openSearch() {
    this.searchService.openSearchOnModule(QUOTATION_ENTITY_TYPE);
  }

  sendQuotation() {
    //TODO
  }

  confirmStatus() {
    //TODO
  }

  instanceOfQuotation = instanceOfQuotation;
  instanceOfCustomerOrder = instanceOfCustomerOrder;

}
