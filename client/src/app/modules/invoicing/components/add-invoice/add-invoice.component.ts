import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { BILLING_TIERS_DOCUMENT_TYPE_OTHER, COUNTRY_CODE_FRANCE } from 'src/app/libs/Constants';
import { formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { City } from 'src/app/modules/miscellaneous/model/City';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { CityService } from 'src/app/modules/miscellaneous/services/city.service';
import { Confrere } from 'src/app/modules/quotation/model/Confrere';
import { Invoice } from 'src/app/modules/quotation/model/Invoice';
import { InvoiceItem } from 'src/app/modules/quotation/model/InvoiceItem';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { Tiers } from 'src/app/modules/tiers/model/Tiers';
import { TiersService } from 'src/app/modules/tiers/services/tiers.service';
import { AppService } from 'src/app/services/app.service';
import { InvoiceService } from '../../services/invoice.service';

@Component({
  selector: 'add-invoice',
  templateUrl: './add-invoice.component.html',
  styleUrls: ['./add-invoice.component.css']
})
export class AddInvoiceComponent implements OnInit {

  invoice: Invoice = {} as Invoice;
  invoiceItems: InvoiceItem[] = new Array<InvoiceItem>;
  invoiceItem: InvoiceItem = {} as InvoiceItem;
  isEditing: boolean = false;
  editMode = true;
  BILLING_TIERS_DOCUMENT_TYPE_OTHER = BILLING_TIERS_DOCUMENT_TYPE_OTHER;
  COUNTRY_CODE_FRANCE = COUNTRY_CODE_FRANCE;

  constructor(private formBuilder: FormBuilder,
    private appService: AppService,
    private invoiceService: InvoiceService,
    private tiersService: TiersService,
    private cityService: CityService,
    private router: Router,
  ) {
  }

  invoiceForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn[] = [] as Array<SortTableColumn>;
  tableAction: SortTableAction[] = [] as Array<SortTableAction>;

  refreshTable: Subject<void> = new Subject<void>();

  ngOnInit() {
    this.addInvoiceItem();
    this.invoice.invoiceItems = this.invoiceItems;

    // Column init
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "label", fieldName: "label", label: "Libellé", isShrinkColumn: true } as SortTableColumn);
    this.displayedColumns.push({ id: "billingType", fieldName: "billingItem.billingType.label", label: "Poste de facturation" } as SortTableColumn);
    this.displayedColumns.push({ id: "preTaxPrice", fieldName: "preTaxPrice", label: "Prix HT", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "vat", fieldName: "vat.label", label: "TVA applicable" } as SortTableColumn);
    this.displayedColumns.push({ id: "vatPrice", fieldName: "vatPrice", label: "Montant de la TVA", valueFonction: this.getVatPrice } as SortTableColumn);
    this.displayedColumns.push({ id: "discountAmount", fieldName: "discountAmount", label: "Remise totale", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "totalPrice", fieldName: "totalPrice", label: "Prix total", valueFonction: this.getTotalPrice } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "delete", actionName: "Supprimer la ligne de facturation", actionClick: (action: SortTableAction, element: any) => {
        this.invoiceItems.splice(this.invoiceItems.indexOf(element), 1);
      }, display: true,
    } as SortTableAction);
  }

  getVatPrice(element: any) {
    if (element && element.vat && element.preTaxPrice)
      return Math.round(element.preTaxPrice * element.vat.rate / 100 * 100) / 100 + " €";
    return "0 €";
  }

  getTotalPrice(element: any) {
    let total = 0;
    if (element && element.preTaxPrice) {
      total += parseFloat(element.preTaxPrice + "");
      if (element.vat)
        total += element.preTaxPrice * element.vat.rate / 100;
      if (element.discountAmount)
        total -= parseFloat(element.discountAmount + "");
      return Math.round(total * 100) / 100 + " €";
    }
    return "0 €";
  }

  addInvoiceItem() {
    if (this.invoiceForm.valid) {
      let newInvoiceItem = {} as InvoiceItem;
      this.invoiceItems.push(newInvoiceItem);
      this.invoiceItem = newInvoiceItem;
      this.refreshTable.next();
    } else {
      this.appService.displaySnackBar("Veuilliez remplir l'ensemble des champs avant d'ajouter une nouvelle ligne de facturation", true, 60);
    }
  }

  selectInvoiceItem(element: InvoiceItem) {
    this.invoiceItem = element;
    this.refreshTable.next();
  }

  saveInvoice() {
    if (this.invoiceForm.valid && this.invoiceItems && this.invoiceItems.length > 0) {
      this.invoiceService.saveInvoice(this.invoice).subscribe(response => {
        if (response)
          this.router.navigate(['/invoicing/', "" + response.id])
      });
    } else {
      this.appService.displaySnackBar("Veuilliez saisir au moins une ligne de facturation valide", true, 60);
    }
  }


  fillTiers(tiers: Tiers) {
    this.invoice.tiers = tiers;
    this.invoice.responsable = undefined;
    this.invoice.responsable = undefined;
  }

  fillConfrere(confrere: Confrere) {
    this.invoice.confrere = confrere;
    this.invoice.tiers = undefined;
    this.invoice.responsable = undefined;
  }

  fillResponsable(responsable: Responsable) {
    this.invoice.responsable = responsable;
    this.tiersService.getTiersByResponsable(responsable.id).subscribe(response => {
      if (this.invoice.responsable != null) {
        this.invoice.responsable.tiers = response;
      }
    })
    this.invoice.tiers = undefined;
  }

  fillPostalCode(city: City) {
    if (!this.invoice.billingLabelCountry)
      this.invoice.billingLabelCountry = city.country;

    if (this.invoice.billingLabelCountry.code == COUNTRY_CODE_FRANCE && city.postalCode != null)
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
}
