import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AzureInvoice } from 'src/app/modules/invoicing/model/AzureInvoice';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { AzureInvoiceService } from '../../../../invoicing/services/azure.invoice.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-azure-invoice',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteAzureInvoiceComponent extends GenericAutocompleteComponent<AzureInvoice, AzureInvoice> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private azureInvoiceService: AzureInvoiceService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<AzureInvoice[]> {
    return this.azureInvoiceService.getAzureInvoiceByInvoiceId(value);
  }

  displayLabel(azureInvoice: AzureInvoice): string {
    if (azureInvoice)
      return "Facture n°" + azureInvoice.invoiceId + " - " + azureInvoice.competentAuthority.label + " - Total TTC : " + (azureInvoice.invoiceTotal) + " €";
    return "";
  }
}
