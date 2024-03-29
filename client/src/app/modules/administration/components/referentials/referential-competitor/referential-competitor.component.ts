import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Competitor } from 'src/app/modules/tiers/model/Competitor';
import { CompetitorService } from 'src/app/modules/tiers/services/competitor.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-competitor',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialCompetitorComponent extends GenericReferentialComponent<Competitor> implements OnInit {
  constructor(private competitorService: CompetitorService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<Competitor> {
    return this.competitorService.addOrUpdateCompetitor(this.selectedEntity!);
  }
  getGetObservable(): Observable<Competitor[]> {
    return this.competitorService.getCompetitors();
  }
}
