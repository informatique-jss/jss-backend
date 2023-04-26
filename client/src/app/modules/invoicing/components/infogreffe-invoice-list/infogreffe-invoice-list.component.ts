import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateTimeForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from 'src/app/services/habilitations.service';
import { InfogreffeInvoice } from '../../model/InfogreffeInvoice';
import { InfogreffeInvoiceService } from '../../services/infogreffe.invoice.service';

@Component({
  selector: 'infogreffe-invoice-list',
  templateUrl: './infogreffe-invoice-list.component.html',
  styleUrls: ['./infogreffe-invoice-list.component.css']
})
export class InfogreffeInvoiceListComponent implements OnInit {

  infogreffeInvoices: InfogreffeInvoice[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  importCsv: string = "";

  constructor(
    private infogreffeInvoiceService: InfogreffeInvoiceService,
    private formBuilder: FormBuilder,
    private habilitationService: HabilitationsService,
    private appService: AppService
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "invoiceNumber", fieldName: "invoiceNumber", label: "N° de facture" } as SortTableColumn);
    this.displayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoiceDateTime", fieldName: "invoiceDateTime", label: "Date", valueFonction: formatDateTimeForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "preTaxPrice", fieldName: "preTaxPrice", label: "Montant HT", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "vatPrice", fieldName: "vatPrice", label: "Montant TVA", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "customerReference", fieldName: "customerReference", label: "Référence JSS" } as SortTableColumn);
    this.displayedColumns.push({ id: "sirenAffaire", fieldName: "sirenAffaire", label: "SIREN" } as SortTableColumn);

    this.infogreffeInvoiceService.getInfogreffeInvoices().subscribe(response => {
      this.infogreffeInvoices = response;
    })
  }

  infogreffeInvoiceForm = this.formBuilder.group({
  });

  canImportInfogreffeInvoice() {
    return this.habilitationService.canImportInfogreffeInvoice();
  }

  importNewCsv() {
    if (this.infogreffeInvoiceForm.valid) {
      this.infogreffeInvoiceService.importCsv(this.importCsv).subscribe(response => {
        this.appService.openRoute(null, '/invoicing/', null);
      })
    }
  }
}
