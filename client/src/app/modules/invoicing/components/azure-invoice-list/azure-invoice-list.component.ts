import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateForSortTable, formatEurosForSortTable } from 'src/app/libs/FormatHelper';
import { SortTableAction } from 'src/app/modules/miscellaneous/model/SortTableAction';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { Appoint } from '../../model/Appoint';
import { AzureInvoice } from '../../model/AzureInvoice';
import { AzureInvoiceService } from '../../services/azure.invoice.service';

@Component({
  selector: 'azure-invoice-list',
  templateUrl: './azure-invoice-list.component.html',
  styleUrls: ['./azure-invoice-list.component.css']
})
export class AzureInvoiceListComponent implements OnInit {
  invoices: AzureInvoice[] | undefined;
  displayedColumns: SortTableColumn[] = [];
  tableAction: SortTableAction[] = [];
  searchLabel: string = "";

  constructor(
    private changeDetectorRef: ChangeDetectorRef,
    private formBuilder: FormBuilder,
    private azureInvoiceService: AzureInvoiceService,
  ) { }

  displayOnlyToCheck: boolean = true;

  ngAfterContentChecked(): void {
    this.changeDetectorRef.detectChanges();
  }

  ngOnInit() {
    this.displayedColumns = [];
    this.displayedColumns.push({ id: "invoiceDate", fieldName: "invoiceDate", label: "Date de facture", valueFonction: formatDateForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "invoiceId", fieldName: "invoiceId", label: "Numéro de facture fournisseur" } as SortTableColumn);
    this.displayedColumns.push({ id: "reference", fieldName: "reference", label: "Référence" } as SortTableColumn);
    this.displayedColumns.push({ id: "competentAuthority", fieldName: "competentAuthority.label", label: "Autorité compétente" } as SortTableColumn);
    this.displayedColumns.push({ id: "invoiceTotal", fieldName: "invoiceTotal", label: "Montant TTC", valueFonction: formatEurosForSortTable } as SortTableColumn);
    this.displayedColumns.push({ id: "toCheck", fieldName: "toCheck", label: "A contrôler ?", valueFonction: (element: AzureInvoice) => { return element.toCheck ? "Oui" : "Non" } } as SortTableColumn);

    this.tableAction.push({
      actionIcon: "edit", actionName: "Contrôler la facture", actionLinkFunction: (action: SortTableAction, element: any) => {
        if (element)
          return ['/invoicing/azure/edit/', element.id];
        return undefined;
      }, display: true,
    } as SortTableAction);
  }

  azureInvoiceForm = this.formBuilder.group({
  });

  getActionLink(action: SortTableColumn, element: any) {
    if (element && action.id == "invoice" && element.invoice)
      return ['/invoicing/view', element.invoice.id];
    return undefined;
  }

  searchInvoices() {
    if (this.azureInvoiceForm.valid) {
      this.azureInvoiceService.getAzureInvoices(this.displayOnlyToCheck).subscribe(response => {
        this.invoices = response;
      })
    }
  }

  refundAppoint(appoint: Appoint) {
  }
}
