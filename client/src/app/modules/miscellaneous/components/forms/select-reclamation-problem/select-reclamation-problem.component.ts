import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SalesReclamationProblem } from '../../../model/SalesReclamationProblem';
import { SalesReclamationProblemService } from 'src/app/modules/tiers/services/sales.reclamation.problem.service';

@Component({
  selector: 'select-reclamation-problem',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectReclamationProblemComponent extends GenericSelectComponent<SalesReclamationProblem> implements OnInit {

  types: SalesReclamationProblem[] = [] as Array<SalesReclamationProblem>;

  constructor(private formBuild: UntypedFormBuilder, private salesReclamationProblemService: SalesReclamationProblemService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.types = [];
    this.salesReclamationProblemService.getReclamationProblems().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: SalesReclamationProblem): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }

}
