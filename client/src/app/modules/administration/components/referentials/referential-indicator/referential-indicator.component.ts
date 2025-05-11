import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { Indicator } from 'src/app/modules/reporting/model/Indicator';
import { IndicatorService } from 'src/app/modules/reporting/services/indicator.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-indicator',
  templateUrl: './referential-indicator.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialIndicatorComponent extends GenericReferentialComponent<Indicator> implements OnInit {
  constructor(private indicatorService: IndicatorService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<Indicator> {
    return this.indicatorService.addOrUpdateIndicator(this.selectedEntity!);
  }
  getGetObservable(): Observable<Indicator[]> {
    return this.indicatorService.getIndicators();
  }
}
