import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { Observable } from 'rxjs';
import { Voucher } from 'src/app/modules/crm/model/Voucher';
import { AppService } from 'src/app/services/app.service';
import { VoucherService } from '../../../../crm/services/voucher.service';
import { GenericAutocompleteComponent } from '../generic-autocomplete/generic-autocomplete.component';

@Component({
  selector: 'autocomplete-voucher',
  templateUrl: '../generic-autocomplete/generic-autocomplete.component.html',
  styleUrls: ['../generic-autocomplete/generic-autocomplete.component.css']
})
export class AutocompleteVoucherComponent extends GenericAutocompleteComponent<Voucher, Voucher> implements OnInit {

  types: Voucher[] = [] as Array<Voucher>;

  constructor(private formBuild: UntypedFormBuilder,
    public voucherDialog: MatDialog, private voucherService: VoucherService,
    public confirmationDialog: MatDialog, private appService3: AppService) {
    super(formBuild, appService3)
  }

  displayLabel(object: Voucher): string {
    return object ? object.code : '';
  }

  searchEntities(value: string): Observable<Voucher[]> {
    return this.voucherService.getVouchersBySearchCode(value);
  }

}
