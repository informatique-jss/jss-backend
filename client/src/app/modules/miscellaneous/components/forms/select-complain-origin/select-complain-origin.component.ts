import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SalesComplainOrigin } from '../../../model/SalesComplainOrigin';
import { SalesComplainOriginService } from 'src/app/modules/tiers/services/sales.complain.origin.service';

@Component({
  selector: 'select-complain-origin',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectComplainOriginComponent extends GenericSelectComponent<SalesComplainOrigin> implements OnInit {

  types: SalesComplainOrigin[] = [] as Array<SalesComplainOrigin>;

  constructor(private formBuild: UntypedFormBuilder, private salesComplainOriginService: SalesComplainOriginService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.salesComplainOriginService.getComplainOrigins().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: SalesComplainOrigin): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }
}
