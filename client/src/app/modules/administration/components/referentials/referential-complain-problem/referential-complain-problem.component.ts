import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';
import { SalesComplainProblem } from 'src/app/modules/miscellaneous/model/SalesComplainProblem';
import { SalesComplainProblemService } from 'src/app/modules/tiers/services/sales.complain.problem.service';

@Component({
  selector: 'referential-complain-problem',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialComplainProblemComponent extends GenericReferentialComponent<SalesComplainProblem> implements OnInit {
  constructor(private salesComplainProblem: SalesComplainProblemService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<SalesComplainProblem> {
    return this.salesComplainProblem.addOrUpdateComplainProblem(this.selectedEntity!);
  }
  getGetObservable(): Observable<SalesComplainProblem[]> {
    return this.salesComplainProblem.getComplainProblems();
  }
}
