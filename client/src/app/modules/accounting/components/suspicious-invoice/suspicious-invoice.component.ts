import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { SuspiciousInvoiceResult } from '../../model/SuspiciousInvoice';
import { SuspiciousInvoiceResultService } from '../../services/suspicious.invoice.result.service';

@Component({
  selector: 'suspicious-invoice',
  templateUrl: './suspicious-invoice.component.html',
  styleUrls: ['./suspicious-invoice.component.css']
})
export class SuspiciousInvoiceComponent implements OnInit {
  faeForm = this.formBuilder.group({});
  displayedColumns: SortTableColumn<SuspiciousInvoiceResult>[] = [];
  displayedColumnsByTiers: SortTableColumn<SuspiciousInvoiceResult>[] = [];
  accountingDate: Date = new Date();
  suspiciousResults: SuspiciousInvoiceResult[] = [];
  suspiciousResultsByTiers: SuspiciousInvoiceResult[] = [];
  selectedTiersId: string | undefined;
  tableAction: SortTableAction<SuspiciousInvoiceResult>[] = [];

  constructor(private suspiciousInvoiceResultService: SuspiciousInvoiceResultService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "idTiers", fieldName: "idTiers", label: "N° tiers" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "tiers", fieldName: "tiers", label: "Tiers" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "idCommercial", fieldName: "idCommercial", label: "Commercial", displayAsEmployee: true } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "idInvoice", fieldName: "idInvoice", label: "Facture" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "createdDate", fieldName: "createdDate", label: "Date de la facture", valueFonction: formatDateForSortTable } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "affaire", fieldName: "affaire", label: "Affaire" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "htAmount", fieldName: "htAmount", label: "Montant HT", valueFonction: formatEurosForSortTable } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "dueDaysNumber", fieldName: "dueDaysNumber", label: "Due depuis (en jours)" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "appliableRate", fieldName: "appliableRate", label: "Taux applicable" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "suspiciousMarkup", fieldName: "suspiciousMarkup", label: "Majoration" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumns.push({ id: "finalAmount", fieldName: "finalAmount", label: "Montant final HT", valueFonction: formatEurosForSortTable } as SortTableColumn<SuspiciousInvoiceResult>);

    this.displayedColumnsByTiers = [];
    this.displayedColumnsByTiers.push({ id: "idTiers", fieldName: "idTiers", label: "N° tiers" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumnsByTiers.push({ id: "tiers", fieldName: "tiers", label: "Tiers" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumnsByTiers.push({ id: "idCommercial", fieldName: "idCommercial", label: "Commercial", displayAsEmployee: true } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumnsByTiers.push({ id: "nbrInvoice", fieldName: "nbrInvoice", label: "Nombre de factures" } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumnsByTiers.push({ id: "htAmount", fieldName: "htAmount", label: "Montant HT", valueFonction: formatEurosForSortTable } as SortTableColumn<SuspiciousInvoiceResult>);
    this.displayedColumnsByTiers.push({ id: "finalAmount", fieldName: "finalAmount", label: "Montant final HT", valueFonction: formatEurosForSortTable } as SortTableColumn<SuspiciousInvoiceResult>);

    this.tableAction.push({
      actionIcon: "point_of_sale", actionName: "Voir le détail de la facture / associer", actionLinkFunction: (action: SortTableAction<SuspiciousInvoiceResult>, element: SuspiciousInvoiceResult) => {
        if (element)
          return ['/invoicing/view', element.idInvoice];
        return undefined;
      }, display: true,
    } as SortTableAction<SuspiciousInvoiceResult>);

    this.refresh();
  }

  refresh() {
    if (this.accountingDate) {
      this.accountingDate = new Date(this.accountingDate.setHours(12));
      this.suspiciousInvoiceResultService.getSuspiciousInvoiceByTiers(this.accountingDate).subscribe(response => this.suspiciousResultsByTiers = response)
      this.suspiciousInvoiceResultService.getSuspiciousInvoice(this.accountingDate).subscribe(response => this.suspiciousResults = response)
    }
  }

  selectTiers(element: SuspiciousInvoiceResult) {
    this.selectedTiersId = element.idTiers + "";
  }

}
