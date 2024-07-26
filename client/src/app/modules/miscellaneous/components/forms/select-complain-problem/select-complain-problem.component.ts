import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { SalesComplainProblem } from '../../../model/SalesComplainProblem';
import { SalesComplainProblemService } from 'src/app/modules/tiers/services/sales.complain.problem.service';

@Component({
  selector: 'select-complain-problem',
  templateUrl: './../generic-select/generic-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.css']
})
export class SelectComplainProblemComponent extends GenericSelectComponent<SalesComplainProblem> implements OnInit {

  types: SalesComplainProblem[] = [] as Array<SalesComplainProblem>;

  constructor(private formBuild: UntypedFormBuilder, private salesComplainProblemService: SalesComplainProblemService,) {
    super(formBuild,)
  }

  initTypes(): void {
    this.types = [];
    this.salesComplainProblemService.getComplainProblems().subscribe(response => {
      this.types = response;
      this.types = this.types.sort((a, b) => a.label.localeCompare(b.label));
    })
  }

  displayLabel(object: SalesComplainProblem): string {
    if (object)
      return object.label + " (" + object.code + ")";
    return "";
  }

}
