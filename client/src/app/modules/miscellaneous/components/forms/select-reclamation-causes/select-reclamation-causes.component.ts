import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SalesReclamationCause } from '../../../model/SalesReclamationCause';
import { SalesReclamationCauseService } from 'src/app/modules/tiers/services/sales.reclamation.cause.service';


@Component({
  selector: 'select-reclamation-cause',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectReclamationCauseComponent extends GenericSelectComponent<SalesReclamationCause> implements OnInit {

  types: SalesReclamationCause[] = [] as Array<SalesReclamationCause>;

  constructor(private formBuild: UntypedFormBuilder, private salesReclamationCauseService: SalesReclamationCauseService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.salesReclamationCauseService.getReclamationCauses().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: SalesReclamationCause): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }

}
