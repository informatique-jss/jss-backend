import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { SalesReclamationProblem } from 'src/app/modules/miscellaneous/model/SalesReclamationProblem';
import { SalesReclamationProblemService } from 'src/app/modules/tiers/services/sales.reclamation.problem.service';

@Component({
  selector: 'referential-reclamation-problem',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialReclamationProblemComponent extends GenericReferentialComponent<SalesReclamationProblem> implements OnInit {
  constructor(private salesReclamationProblem: SalesReclamationProblemService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<SalesReclamationProblem> {
    return this.salesReclamationProblem.addOrUpdateReclamationProblem(this.selectedEntity!);
  }
  getGetObservable(): Observable<SalesReclamationProblem[]> {
    return this.salesReclamationProblem.getReclamationProblems();
  }
}
