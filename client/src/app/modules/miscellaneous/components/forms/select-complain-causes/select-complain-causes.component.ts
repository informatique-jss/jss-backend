import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SalesComplainCause } from '../../../model/SalesComplainCause';
import { SalesComplainCauseService } from 'src/app/modules/tiers/services/sales.complain.cause.service';


@Component({
  selector: 'select-complain-cause',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectComplainCauseComponent extends GenericSelectComponent<SalesComplainCause> implements OnInit {

  types: SalesComplainCause[] = [] as Array<SalesComplainCause>;

  constructor(private formBuild: UntypedFormBuilder, private salesComplainCauseService: SalesComplainCauseService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.salesComplainCauseService.getComplainCauses().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: SalesComplainCause): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }

}
