import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { InfogreffeInvoiceService } from 'src/app/modules/invoicing/services/infogreffe.invoice.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { InfogreffeInvoice } from '../../../../invoicing/model/InfogreffeInvoice';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-infogreffe-invoice',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteInfogreffeInvoiceComponent extends GenericAutocompleteComponent<InfogreffeInvoice, InfogreffeInvoice> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private infogreffeInvoiceService: InfogreffeInvoiceService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<InfogreffeInvoice[]> {
    return this.infogreffeInvoiceService.getInfogreffeInvoicesByCustomerReference(value);
  }

  displayLabel(infogreffeInvoice: InfogreffeInvoice): string {
    if (infogreffeInvoice)
      return "Facture n°" + infogreffeInvoice.customerReference + " - " + infogreffeInvoice.competentAuthority.label + " - Total TTC : " + (infogreffeInvoice.preTaxPrice + infogreffeInvoice.vatPrice) + " €";
    return "";
  }
}
