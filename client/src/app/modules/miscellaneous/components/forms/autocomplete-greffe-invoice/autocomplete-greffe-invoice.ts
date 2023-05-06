import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { OwncloudGreffeInvoice } from 'src/app/modules/quotation/model/OwncloudGreffeInvoice';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { OwncloudGreffeInvoiceService } from '../../../../invoicing/services/owncloud.greffe.invoice.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-greffe-invoice',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteGreffeInvoiceComponent extends GenericAutocompleteComponent<OwncloudGreffeInvoice, OwncloudGreffeInvoice> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder,
    private owncloudGreffeInvoiceService: OwncloudGreffeInvoiceService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  searchEntities(value: string): Observable<OwncloudGreffeInvoice[]> {
    return this.owncloudGreffeInvoiceService.getOwncloudGreffeInvoiceByNumero(value);
  }

  displayLabel(greffeInvoice: OwncloudGreffeInvoice): string {
    if (greffeInvoice)
      return "Facture n°" + greffeInvoice.numero + " - " + greffeInvoice.owncloudGreffeFile.competentAuthority.label + " - Total TTC : " + greffeInvoice.totalPrice + " €";
    return "";
  }
}
