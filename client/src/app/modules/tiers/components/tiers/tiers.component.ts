import { AfterContentChecked, ChangeDetectorRef, Component, Input, OnInit, ViewChild, ViewEncapsulation } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Subscription } from 'rxjs';
import { ConfirmDialogComponent } from 'src/app/modules/miscellaneous/components/confirm-dialog/confirm-dialog.component';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AffaireSearch } from 'src/app/modules/quotation/model/AffaireSearch';
import { OrderingSearch } from 'src/app/modules/quotation/model/OrderingSearch';
import { QuotationSearch } from 'src/app/modules/quotation/model/QuotationSearch';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { TIERS_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { SearchService } from 'src/app/services/search.service';
import { InvoiceSearch } from '../../../invoicing/model/InvoiceSearch';
import { Responsable } from '../../model/Responsable';
import { RffSearch } from '../../model/RffSearch';
import { Tiers } from '../../model/Tiers';
import { TiersService } from '../../services/tiers.service';
import { ResponsableMainComponent } from '../responsable-main/responsable-main.component';
import { SettlementBillingComponent } from '../settlement-billing/settlement-billing.component';
import { PrincipalComponent } from '../tiers-main/tiers-main.component';

@Component({
  selector: 'tiers',
  templateUrl: './tiers.component.html',
  styleUrls: ['./tiers.component.css'],
  encapsulation: ViewEncapsulation.None,
})
export class TiersComponent implements OnInit, AfterContentChecked {

  tiers: Tiers = {} as Tiers;
  editMode: boolean = false;
  createMode: boolean = false;

  TIERS_ENTITY_TYPE = TIERS_ENTITY_TYPE;

  orderingSearch: OrderingSearch = {} as OrderingSearch;
  quotationSearch: QuotationSearch = {} as QuotationSearch;
  provisionSearch: AffaireSearch = {} as AffaireSearch;
  invoiceSearch: InvoiceSearch = {} as InvoiceSearch;
  responsableAccountSearch: Tiers | undefined;
  rffSearch: RffSearch | undefined;

  saveObservableSubscription: Subscription = new Subscription;

  selectedTabIndex = 0;

  @ViewChild('tabs', { static: false }) tabs: any;

  @ViewChild(PrincipalComponent) principalFormComponent: PrincipalComponent | undefined;
  @ViewChild(SettlementBillingComponent) documentSettlementBillingComponent: SettlementBillingComponent | undefined;
  @ViewChild(ResponsableMainComponent) responsableMainComponent: ResponsableMainComponent | undefined;

  @Input() idTiers: number | undefined;
  @Input() idResponsable: number | undefined;

  constructor(private appService: AppService,
    private tiersService: TiersService,
    private activatedRoute: ActivatedRoute,
    protected searchService: SearchService,
    private constantService: ConstantService,
    public confirmationDialog: MatDialog,
    private changeDetectorRef: ChangeDetectorRef) { }

  ngOnInit() {
    if (!this.idTiers && !this.idResponsable)
      this.appService.changeHeaderTitle("Tiers / Responsables");

    let idTiers: number = this.activatedRoute.snapshot.params.id;
    if (this.idTiers)
      idTiers = this.idTiers;

    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    // Load by responsable
    if (this.idResponsable || url != undefined && url != null && url[0] != undefined && url[1] != undefined && url[0].path == "tiers" && url[1].path == "responsable") {
      this.tiersService.getTiersByResponsable(this.idResponsable ? this.idResponsable : idTiers).subscribe(response => {
        this.tiers = response;
        this.tiersService.setCurrentViewedTiers(this.tiers);

        if (!this.idTiers && !this.idResponsable)
          this.appService.changeHeaderTitle(this.tiers.denomination != null ? this.tiers.denomination : this.tiers.firstname + " " + this.tiers.lastname);
        this.toggleTabs();
        this.selectedTabIndex = 2;
        this.responsableMainComponent?.setSelectedResponsableId(this.idResponsable ? this.idResponsable : idTiers);

        this.loadQuotationFilter();
      })
      // Load by tiers
    } else if (idTiers != null && idTiers != undefined) {
      this.tiersService.getTiers(idTiers).subscribe(response => {
        this.tiers = response;
        this.tiersService.setCurrentViewedTiers(this.tiers);
        this.changeHeader();
        this.toggleTabs();

        this.loadQuotationFilter();

        this.rffSearch = {} as RffSearch;
        this.rffSearch.tiers = { entityId: this.tiers.id } as IndexEntity;
        this.rffSearch.isHideCancelledRff = false;

        let start = new Date();
        let d = new Date(start.getTime());
        d.setFullYear(d.getFullYear() - 2);
        this.rffSearch.startDate = d;

        let end = new Date();
        let d2 = new Date(end.getTime());
        d2.setFullYear(d2.getFullYear() + 2);
        this.rffSearch.endDate = d2;
      })
    } else if (this.createMode == false) {
      // Blank page
      if (!this.idTiers && !this.idResponsable)
        this.appService.changeHeaderTitle("Tiers / Responsables");
    }

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveTiers()
        else if (this.tiers && this.tiers.id)
          this.editTiers()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  loadQuotationFilter() {
    this.orderingSearch.customerOrders = [this.tiers];
    this.invoiceSearch.customerOrders = [this.tiers];
    this.quotationSearch.customerOrders = [this.tiers];
    this.provisionSearch.customerOrders = [this.tiers];

    if (this.tiers.responsables) {
      this.orderingSearch.customerOrders.push(...this.tiers.responsables);
      this.invoiceSearch.customerOrders.push(...this.tiers.responsables);
      this.quotationSearch.customerOrders.push(...this.tiers.responsables);
      this.provisionSearch.customerOrders.push(...this.tiers.responsables);
    }

    this.responsableAccountSearch = this.tiers;
  }

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }


  changeHeader() {
    if (this.tiers.denomination != null) {
      if (!this.idTiers && !this.idResponsable)
        this.appService.changeHeaderTitle(this.tiers.denomination);
    } else if (this.tiers.firstname != null) {
      if (!this.idTiers && !this.idResponsable)
        this.appService.changeHeaderTitle(this.tiers.firstname + " " + this.tiers.lastname);
    }
  }
  changePageHeader($event: any) {
    if ($event.tab.textLabel != "Responsable(s)") {
      if (this.tiers.id != null && this.tiers.id != undefined)
        this.changeHeader();
    }
  }

  isTiersTypeProspect(): boolean {
    return this.tiers && this.tiers.tiersType && this.constantService.getTiersTypeProspect().id == this.tiers.tiersType.id;
  }

  toggleTabs() {
    if (this.tabs != undefined)
      this.tabs.realignInkBar();
  }

  saveTiers() {
    if (this.getFormsStatus())
      this.tiersService.addOrUpdateTiers(this.tiers).subscribe(response => {
        this.tiers = response;
        this.editMode = false;
        this.appService.openRoute(null, '/tiers/' + this.tiers.id, null);
      })
  }

  getFormsStatus(): boolean {
    let principalFormStatus = this.principalFormComponent?.getFormStatus();
    let documentSettlementBillingFormStatus = this.documentSettlementBillingComponent?.getFormStatus();
    let responsableMainComponentFormStatus = this.responsableMainComponent?.getFormStatus();
    let errorMessages: string[] = [] as Array<string>;
    if (!principalFormStatus)
      errorMessages.push("Onglet Fiche du tiers" + (this.principalFormComponent!.principalForm.errors ? " : " + this.principalFormComponent!.principalForm.errors["notFilled"] : ""));
    if (!this.isTiersTypeProspect() && !documentSettlementBillingFormStatus)
      errorMessages.push("Onglet Pièces, réglements, facturations & relances" + (this.documentSettlementBillingComponent!.settlementBillingForm.errors ? " : " + this.documentSettlementBillingComponent?.settlementBillingForm.errors["notFilled"] : ""));
    if (!responsableMainComponentFormStatus)
      errorMessages.push("Onglet Responsable" + (this.responsableMainComponent!.principalForm.errors ? " : " + this.responsableMainComponent!.principalForm.errors["notFilled"] : ""));
    if (errorMessages.length > 0) {
      let errorMessage = "Les onglets suivants ne sont pas correctement remplis. Veuillez les compléter avant de sauvegarder : " + errorMessages.join(" / ");
      this.appService.displaySnackBar(errorMessage, true, 15);
      return false;
    }
    return true;
  }

  editTiers() {
    this.editMode = true;
  }

  createTiers() {
    this.createMode = true;
    this.editMode = true;
    this.tiers = {} as Tiers;
    this.tiers.language = this.constantService.getLanguageFrench();
    this.tiers.isProvisionalPaymentMandatory = true;
    this.tiers.responsables = [] as Array<Responsable>;
    this.tiersService.setCurrentViewedTiers(this.tiers);
    if (!this.idTiers && !this.idResponsable)
      this.appService.changeHeaderTitle("Nouveau Tiers / Responsable");
    this.toggleTabs();
  }

  openSearch() {
    this.searchService.openSearchOnModule(TIERS_ENTITY_TYPE);
  }

  deleteTiers() {
    const dialogRef = this.confirmationDialog.open(ConfirmDialogComponent, {
      maxWidth: "400px",
      data: {
        title: "Supprimer le tiers n°" + this.tiers.id,
        content: "Êtes-vous sûr de vouloir supprimer ce tiers ? Cette action est irréversible !",
        closeActionText: "Annuler",
        validationActionText: "Supprimer"
      }
    });

    dialogRef.afterClosed().subscribe(dialogResult => {
      if (dialogResult)
        this.tiersService.deleteTiers(this.tiers).subscribe(res => {
          this.appService.openRoute(null, '/tiers/', null);
        });
    });
  }

}
