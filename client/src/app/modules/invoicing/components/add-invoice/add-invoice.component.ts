import { Component, OnInit } from '@angular/core';
import { AbstractControl, FormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { Subject, Subscription } from 'rxjs';
import { validateVat } from 'src/app/libs/CustomFormsValidatorsHelper';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { instanceOfCustomerOrder } from 'src/app/libs/TypeHelper';
import { Attachment } from 'src/app/modules/miscellaneous/model/Attachment';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { CompetentAuthority } from 'src/app/modules/miscellaneous/model/CompetentAuthority';
import { Country } from 'src/app/modules/miscellaneous/model/Country';
import { Provider } from 'src/app/modules/miscellaneous/model/Provider';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { InvoiceItem } from 'src/app/modules/quotation/model/InvoiceItem';
import { Provision } from 'src/app/modules/quotation/model/Provision';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { INVOICE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { IndexEntity } from '../../../../routing/search/IndexEntity';
import { IndexEntityService } from '../../../../routing/search/index.entity.service';
import { BillingItem } from '../../../miscellaneous/model/BillingItem';
import { CompetentAuthorityService } from '../../../miscellaneous/services/competent.authority.service';
import { CustomerOrder } from '../../../quotation/model/CustomerOrder';
import { CustomerOrderService } from '../../../quotation/services/customer.order.service';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { InvoiceService } from '../../services/invoice.service';

@Component({
  selector: 'add-invoice',
  templateUrl: './add-invoice.component.html',
  styleUrls: ['./add-invoice.component.css']
})
export class AddInvoiceComponent implements OnInit {

  invoice: Invoice = { isInvoiceFromProvider: true, manualPaymentType: this.contantService.getPaymentTypeVirement() } as Invoice;
  invoiceItems: InvoiceItem[] = new Array<InvoiceItem>;
  invoiceItem: InvoiceItem = {} as InvoiceItem;
  isEditing: boolean = false;
  editMode = true;

  countryFrance: Country = this.contantService.getCountryFrance();
  billingLableTypeOther = this.contantService.getBillingLabelTypeOther();

  saveObservableSubscription: Subscription = new Subscription;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private invoiceService: InvoiceService,
    private activatedRoute: ActivatedRoute,
    private tiersService: TiersService,
    private cityService: CityService,
    private responsableService: ResponsableService,
    private customerOrderService: CustomerOrderService,
    private contantService: ConstantService,
    public deboursAmontTaxableDialog: MatDialog,
    private competentAuthorityService: CompetentAuthorityService,
    public deboursAmontInvoicedDialog: MatDialog,
    private indexEntityService: IndexEntityService,
  ) {
  }

  invoiceForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;
  tableAction: SortTableAction[] = [] as Array<SortTableAction>;
  attachmentTypeInvoice = this.contantService.getAttachmentTypeInvoice();
  indexedCustomerOrder: IndexEntity | undefined;
  idInvoiceForCreditNote: string | undefined;

  refreshTable: Subject<void> = new Subject<void>();

  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;

  inIdProvision: number | undefined;

  ngOnInit() {
    let idInvoice = this.activatedRoute.snapshot.params.id;
    this.inIdProvision = this.activatedRoute.snapshot.params.idProvision;
    this.idInvoiceForCreditNote = this.activatedRoute.snapshot.params.idInvoice;

    let idCustomerOrder = this.activatedRoute.snapshot.params.idCustomerOrder;
    let idCompetentAuhority = this.activatedRoute.snapshot.params.idCompetentAuhority;

    let url: UrlSegment[] = this.activatedRoute.snapshot.url;

    if (url != undefined && url != null && url[2] != undefined && url[2].path == "azure" && this.inIdProvision) {
      this.invoiceService.createInvoiceFromAzureInvoice(idInvoice, this.inIdProvision).subscribe(generatedInvoice => {
        this.invoice = generatedInvoice;

        this.customerOrderService.getCustomerOrder(this.invoice.customerOrderForInboundInvoice.id).subscribe(customerOrder => {
          if (customerOrder && instanceOfCustomerOrder(customerOrder)) {
            this.invoice.customerOrderForInboundInvoice = customerOrder;
            if (customerOrder.assoAffaireOrders)
              for (let asso of customerOrder.assoAffaireOrders)
                if (asso.provisions)
                  for (let provision of asso.provisions)
                    if (provision.id == this.inIdProvision)
                      this.invoice.provision = provision;
          }
        })
        this.invoice.provision
      })
    } else if (idInvoice != null && idInvoice != "null") {
      this.invoiceService.getInvoiceById(idInvoice).subscribe(response => {
        this.invoice = response;
        this.invoiceItems = this.invoice.invoiceItems;
        this.appService.changeHeaderTitle("Facture n°" + this.invoice.id);
      });
    } else if (this.idInvoiceForCreditNote != null && this.idInvoiceForCreditNote != "null") {
      this.invoiceService.getInvoiceById(parseInt(this.idInvoiceForCreditNote)).subscribe(response => {
        this.invoice = {} as Invoice;
        this.invoice.invoiceItems = [{} as InvoiceItem];
        this.invoiceItems = this.invoice.invoiceItems;
        this.selectInvoiceItem(this.invoiceItems[0]);
        this.invoice.isInvoiceFromProvider = false;
        this.invoice.isProviderCreditNote = true;
        this.invoice.manualPaymentType = this.contantService.getPaymentTypeVirement();
        if (response && response.competentAuthority)
          this.invoice.competentAuthority = response.competentAuthority;
        this.appService.changeHeaderTitle("Saisir un avoir sur la facture n°" + this.idInvoiceForCreditNote);
      });
    } else {
      this.addInvoiceItem();
      this.invoice.isInvoiceFromProvider = true;
      if (idCompetentAuhority)
        this.competentAuthorityService.getCompetentAuthorityById(idCompetentAuhority).subscribe(competentAuthority => this.invoice.competentAuthority = competentAuthority);

      if (idCustomerOrder) {
        this.indexEntityService.getCustomerOrdersByKeyword(idCustomerOrder).subscribe(indexEntity => {
          if (indexEntity) {
            this.indexedCustomerOrder = indexEntity[0];
            this.fillCustomerOrder(indexEntity[0]);
          }
        });
      }
    }
    this.invoice.invoiceItems = this.invoiceItems;
    this.appService.changeHeaderTitle("Nouvelle facture");

    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "billingType", fieldName: "billingItem.billingType.label", label: "Poste de facturation" } as SortTableColumn);
    this.displayedColumns.push({ id: "preTaxPrice", fieldName: "preTaxPrice", label: "Prix HT", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "vat", fieldName: "vat.label", label: "TVA applicable" } as SortTableColumn);
    this.displayedColumns.push({ id: "vatPrice", fieldName: "vatPrice", label: "Montant de la TVA", valueFonction: this.getVatPrice } as SortTableColumn);
    this.displayedColumns.push({ id: "discountAmount", fieldName: "discountAmount", label: "Remise totale", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: (element: any) => { return this.getTotalPriceValue(element) + " €" } } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer la ligne de facturation", actionClick: (action: SortTableAction, element: any) => {
        element.nonTaxableAmount = null;
        this.invoiceItems.splice(this.invoiceItems.indexOf(element), 1);
        this.refreshTable.next();
      }, display: true,
    } as SortTableAction);

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        this.saveInvoice()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  getVatPrice(element: any) {
    if (element && element.vat && element.preTaxPrice)
      return Math.round(element.preTaxPrice * element.vat.rate / 100 * 100) / 100 + " €";
    return "0 €";
  }

  emptyProvider() {
    if (this.invoice && this.invoice.isInvoiceFromProvider == false)
      this.invoice.provider = undefined;
  }

  getTotalPriceValue(element: any): number {
    let total = 0;
    if (element && element.preTaxPrice) {
      total += parseFloat(element.preTaxPrice + "");
      if (element.discountAmount)
        total -= parseFloat(element.discountAmount + "");
      if (element.vat)
        total += (parseFloat(element.preTaxPrice + "") + (parseFloat(element.discountAmount + "") ? parseFloat(element.discountAmount + "") : 0)) * element.vat.rate / 100;
      return Math.round(total * 100) / 100;
    }
    return 0;
  }

  getInvoiceTotal(): number {
    let total = 0;
    if (this.invoiceItems)
      for (let invoiceItem of this.invoiceItems)
        total += this.getTotalPriceValue(invoiceItem);

    return Math.round(total * 100) / 100;
  }

  addInvoiceItem() {
    if (this.invoiceForm.valid) {
      let newInvoiceItem = {} as InvoiceItem;
      this.invoiceItems.push(newInvoiceItem);
      this.invoiceItem = newInvoiceItem;
      this.refreshTable.next();
    } else {
      this.appService.displaySnackBar("Veuillez remplir l'ensemble des champs avant d'ajouter une nouvelle ligne de facturation", true, 15);
    }
  }

  selectInvoiceItem(element: InvoiceItem) {
    if (this.editMode && !this.invoice.id) {
      this.invoiceItem = element;
      this.refreshTable.next();
    }
  }

  saveInvoice() {
    if (this.invoiceForm.valid && (this.invoiceItems && this.invoiceItems.length > 0 || this.invoice.competentAuthority != null && this.invoice.customerOrderForInboundInvoice != null) || this.invoice.id) {
      if (this.idInvoiceForCreditNote) {
        this.invoiceService.saveCreditNote(this.invoice, this.idInvoiceForCreditNote).subscribe(response => {
          if (response)
            this.appService.openRoute(null, '/invoicing/view/' + response.id, null);
        });
      } else {
        this.invoiceService.saveInvoice(this.invoice).subscribe(response => {
          if (response)
            this.appService.openRoute(null, '/invoicing/view/' + response.id, null);
        });
      }
    } else {
      this.appService.displaySnackBar("Veuillez saisir au moins une ligne de facturation valide", true, 15);
    }
  }


  fillTiers(tiers: IndexEntity) {
    this.tiersService.getTiers(tiers.entityId).subscribe(response => {
      this.invoice.tiers = response;
      this.invoice.responsable = undefined;
      this.invoice.confrere = undefined;
      this.invoice.provider = undefined;
      this.invoice.competentAuthority = undefined;
    })
  }

  fillConfrere(confrere: Confrere) {
    this.invoice.confrere = confrere;
    this.invoice.tiers = undefined;
    this.invoice.responsable = undefined;
    this.invoice.competentAuthority = undefined;
  }

  fillProvider(provider: Provider) {
    this.invoice.provider = provider;
    this.invoice.confrere = undefined;
    this.invoice.tiers = undefined;
    this.invoice.responsable = undefined;
    this.invoice.competentAuthority = undefined;
  }

  fillCompetentAuthority(competentAuthority: CompetentAuthority) {
    this.invoice.competentAuthority = competentAuthority;
    this.invoice.provider = undefined;
    this.invoice.confrere = undefined;
    this.invoice.tiers = undefined;
    this.invoice.responsable = undefined;
  }

  fillResponsable(responsable: IndexEntity) {
    this.responsableService.getResponsable(responsable.entityId).subscribe(response => {
      this.invoice.responsable = response;
      this.invoice.competentAuthority = undefined;
      this.invoice.provider = undefined;
      this.invoice.confrere = undefined;
      this.invoice.tiers = undefined;
    })
  }

  fillCustomerOrder(customerOrderIndexed: IndexEntity) {
    this.customerOrderService.getCustomerOrder(customerOrderIndexed.entityId).subscribe(response => {
      if (response) {
        this.invoice.customerOrderForInboundInvoice = response as CustomerOrder;
        if (this.inIdProvision)
          for (let asso of response.assoAffaireOrders)
            for (let provision of asso.provisions)
              if (provision.id == this.inIdProvision)
                this.invoice.provision = provision;
      } else
        this.indexedCustomerOrder = undefined;
    })
  }

  fillPostalCode(city: City) {
    if (!this.invoice.billingLabelCountry)
      this.invoice.billingLabelCountry = city.country;

    if (this.invoice.billingLabelCountry.id == this.countryFrance.id && city.postalCode != null && !this.invoice.billingLabelPostalCode)
      this.invoice.billingLabelPostalCode = city.postalCode;
  }

  fillCity(postalCode: string) {
    this.cityService.getCitiesFilteredByPostalCode(postalCode).subscribe(response => {
      if (response != null && response != undefined && response.length == 1) {
        let city = response[0];
        if (this.invoice.billingLabelCountry == null || this.invoice.billingLabelCountry == undefined)
          this.invoice.billingLabelCountry = city.country;

        this.invoice.billingLabelCity = city;
      }
    })
  }

  updateAttachments(attachments: Attachment[]) {
    if (attachments && this.invoice) {
      this.invoice.attachments = attachments;
    }
  }

  isManualInvoice(): boolean {
    return !this.invoice.id || !this.invoice.customerOrder;
  }

  fillBillingItem(invoiceItem: InvoiceItem, billingItem: BillingItem) {
    if (invoiceItem && billingItem)
      if (billingItem.billingType && billingItem.billingType.isOverrideVat && billingItem.billingType.vat) {
        invoiceItem.vat = billingItem.billingType.vat;
        return;
      }
    if (billingItem.billingType && (billingItem.billingType.isDebour || billingItem.billingType.isFee))
      if (billingItem.billingType.isNonTaxable)
        invoiceItem.vat = this.contantService.getVatZero();
      else
        invoiceItem.vat = this.contantService.getVatDeductible();
  }

  checkVAT(fieldName: string): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const root = control.root as UntypedFormGroup;
      const fieldValue = root.get(fieldName)?.value;
      if (!this.invoice.billingLabelIsIndividual && (fieldValue == undefined || fieldValue == null || fieldValue.length == 0 || !validateVat(fieldValue)))
        return {
          notFilled: true
        };
      return null;
    };
  }

  fillPreTaxPricedReinvoiced(invoiceItem: InvoiceItem, invoice: Invoice) {
    if (invoice.isInvoiceFromProvider)
      invoiceItem.preTaxPriceReinvoiced = invoiceItem.preTaxPrice;
  }

  getProvisionForCustomerOrder(): Provision[] {
    let outProvisions = [];
    if (this.invoice && this.invoice.customerOrderForInboundInvoice && this.invoice.customerOrderForInboundInvoice.assoAffaireOrders) {
      for (let asso of this.invoice.customerOrderForInboundInvoice.assoAffaireOrders) {
        for (let provision of asso.provisions)
          outProvisions.push(provision)
      }
    }
    return outProvisions;
  }
}
