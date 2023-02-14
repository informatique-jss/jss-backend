import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
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
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { INVOICE_ENTITY_TYPE } from 'src/app/routing/search/search.component';
import { AppService } from 'src/app/services/app.service';
import { IndexEntity } from '../../../../routing/search/IndexEntity';
import { BillingItem } from '../../../miscellaneous/model/BillingItem';
import { CustomerOrder } from '../../../quotation/model/CustomerOrder';
import { Debour } from '../../../quotation/model/Debour';
import { CustomerOrderService } from '../../../quotation/services/customer.order.service';
import { ResponsableService } from '../../../tiers/services/responsable.service';
import { InvoiceService } from '../../services/invoice.service';
import { DeboursAmountTaxableDialogComponent } from '../debours-amount-taxable-dialog/debours-amount-taxable-dialog.component';

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
  ) {
  }

  invoiceForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;
  tableAction: SortTableAction[] = [] as Array<SortTableAction>;
  displayedColumnsDebours: SortTableColumn[] = [] as Array<SortTableColumn>;
  displayedColumnsSelectedDebours: SortTableColumn[] = [] as Array<SortTableColumn>;
  tableActionSelectedDebours: SortTableAction[] = [] as Array<SortTableAction>;
  attachmentTypeInvoice = this.contantService.getAttachmentTypeInvoice();
  indexedCustomerOrder: IndexEntity | undefined;
  debours: Debour[] | undefined;
  selectedDebours: Debour[] | undefined;

  refreshTable: Subject<void> = new Subject<void>();

  INVOICE_ENTITY_TYPE = INVOICE_ENTITY_TYPE;

  ngOnInit() {
    let idInvoice = this.activatedRoute.snapshot.params.id;

    if (idInvoice != null && idInvoice != "null")
      this.invoiceService.getInvoiceById(idInvoice).subscribe(response => {
        this.invoice = response;
        this.invoiceItems = this.invoice.invoiceItems;
        this.appService.changeHeaderTitle("Facture n°" + this.invoice.id);
      });
    else {
      this.addInvoiceItem();
      this.invoice.isInvoiceFromProvider = true;
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
        this.invoiceItems.splice(this.invoiceItems.indexOf(element), 1);
        this.refreshTable.next();
      }, display: true,
    } as SortTableAction);

    // Debours
    this.displayedColumnsDebours = [];
    this.displayedColumnsDebours.push({ id: "billingType", fieldName: "billingType.label", label: "Débour" } as SortTableColumn);
    this.displayedColumnsDebours.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.displayedColumnsDebours.push({ id: "debourAmount", fieldName: "debourAmount", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumnsDebours.push({ id: "paymentType", fieldName: "paymentType.label", label: "Type de paiement" } as SortTableColumn);
    this.displayedColumnsDebours.push({ id: "paymentDateTime", fieldName: "paymentDateTime", label: "Date de paiement", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumnsDebours.push({ id: "comments", fieldName: "comments", label: "Commentaires", isShrinkColumn: true } as SortTableColumn);

    // Debours
    this.displayedColumnsSelectedDebours = [];
    this.displayedColumnsSelectedDebours.push({ id: "billingType", fieldName: "billingType.label", label: "Débour" } as SortTableColumn);
    this.displayedColumnsSelectedDebours.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.displayedColumnsSelectedDebours.push({ id: "debourAmount", fieldName: "debourAmount", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumnsSelectedDebours.push({ id: "nonTaxableAmount", fieldName: "nonTaxableAmount", label: "Dont montant non taxable", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumnsSelectedDebours.push({ id: "paymentType", fieldName: "paymentType.label", label: "Type de paiement" } as SortTableColumn);
    this.displayedColumnsSelectedDebours.push({ id: "paymentDateTime", fieldName: "paymentDateTime", label: "Date de paiement", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumnsSelectedDebours.push({ id: "comments", fieldName: "comments", label: "Commentaires", isShrinkColumn: true } as SortTableColumn);

    this.tableActionSelectedDebours.push({
      actionIcon: "delete", actionName: "Supprimer le débour de cette facture", actionClick: (action: SortTableAction, element: any) => {
        if (this.selectedDebours && this.debours) {
          this.selectedDebours.splice(this.selectedDebours.indexOf(element), 1);
        }
      }, display: true,
    } as SortTableAction);
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
      this.invoiceService.saveInvoice(this.invoice).subscribe(response => {
        if (response)
          this.appService.openRoute(null, '/invoicing/view/' + response.id, null);
      });
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
    this.fillDebours();
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
        this.debours = [];
        this.selectedDebours = [];
        this.invoice.customerOrderForInboundInvoice = response as CustomerOrder;
        this.fillDebours();
      } else
        this.indexedCustomerOrder = undefined;
    })
  }

  fillDebours() {
    if (this.invoice.customerOrderForInboundInvoice && this.invoice.competentAuthority && this.invoice.customerOrderForInboundInvoice.assoAffaireOrders) {
      this.debours = [];
      this.invoice.invoiceItems = [];
      for (let asso of this.invoice.customerOrderForInboundInvoice.assoAffaireOrders)
        if (asso.provisions)
          for (let provision of asso.provisions)
            if (provision.debours)
              for (let debour of provision.debours)
                if (debour.competentAuthority.id == this.invoice.competentAuthority.id && !debour.invoiceItem)
                  this.debours.push(debour);
    }
  }

  selectDebour(debour: Debour) {
    if (debour.invoiceItem) {
      this.appService.displaySnackBar("Ce débours a déjà été attaché à la facture n°" + debour.invoiceItem.invoice.id, false, 10);
      return;
    }
    if (!this.selectedDebours || this.getIndexOfDebours(debour, this.selectedDebours) < 0) {
      if (debour && debour.billingType.isNonTaxable) {
        if (!this.selectedDebours)
          this.selectedDebours = [];
        this.selectedDebours.push(debour);
        this.refreshTable.next();
        return;
      }

      const dialogRef = this.deboursAmontTaxableDialog.open(DeboursAmountTaxableDialogComponent, {
        maxWidth: "300px",
      });

      dialogRef.componentInstance.maxAmount = debour.debourAmount;

      dialogRef.afterClosed().subscribe(dialogResult => {
        if (dialogResult != null && this.debours) {
          if (!this.selectedDebours)
            this.selectedDebours = [];
          debour.nonTaxableAmount = parseFloat(dialogResult);
          this.selectedDebours.push(debour);
          this.refreshTable.next();
        }
      });
    } else {
      this.appService.displaySnackBar("Ce débours a déjà été sélectionné", false, 10);
    }
  }

  getIndexOfDebours(debour: Debour, debourList: Debour[]) {
    if (debour && debourList)
      for (let i = 0; i < debourList.length; i++)
        if (debourList[i].id == debour.id)
          return i;
    return -1;
  }

  displayDeboursTable() {
    return this.invoice.customerOrderForInboundInvoice && this.debours && this.invoice.competentAuthority;
  }

  getDeboursTotal(): number {
    let total = 0;
    if (this.selectedDebours)
      for (let debour of this.selectedDebours)
        total += debour.debourAmount;
    return Math.round(total * 100) / 100;
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
    if (billingItem.billingType && billingItem.billingType.isDebour)
      if (billingItem.billingType.isNonTaxable)
        invoiceItem.vat = this.contantService.getVatZero();
      else
        invoiceItem.vat = this.contantService.getVatDeductible();
  }
}
