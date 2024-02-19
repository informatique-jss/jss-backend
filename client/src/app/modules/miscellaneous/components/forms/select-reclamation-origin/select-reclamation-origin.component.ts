import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SalesReclamationOrigin } from '../../../model/SalesReclamationOrigin';
import { SalesReclamationOriginService } from 'src/app/modules/tiers/services/sales.reclamation.origin.service';

@Component({
  selector: 'select-reclamation-origin',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectReclamationOriginComponent extends GenericSelectComponent<SalesReclamationOrigin> implements OnInit {

  types: SalesReclamationOrigin[] = [] as Array<SalesReclamationOrigin>;

  constructor(private formBuild: UntypedFormBuilder, private salesReclamationOriginService: SalesReclamationOriginService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.salesReclamationOriginService.getReclamationOrigins().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: SalesReclamationOrigin): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }
}
