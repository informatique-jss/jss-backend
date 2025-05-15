import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { copyObject } from 'src/app/libs/GenericHelper';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { Indicator } from 'src/app/modules/reporting/model/Indicator';
import { Kpi } from 'src/app/modules/reporting/model/Kpi';
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

  employeesToCopy: Employee[] = [];

  getAddOrUpdateObservable(): Observable<Indicator> {
    if (this.selectedEntity)
      if (this.selectedEntity.kpis)
        for (let kpi of this.selectedEntity.kpis)
          if (kpi.applicationDate) {
            kpi.applicationDate = new Date(kpi.applicationDate.setHours(12));
          }
    return this.indicatorService.addOrUpdateIndicator(this.selectedEntity!);
  }
  getGetObservable(): Observable<Indicator[]> {
    return this.indicatorService.getIndicators();
  }

  copyOnOtherEmployees(kpiIndex: number) {
    if (this.employeesToCopy && this.selectedEntity)
      for (let employee of this.employeesToCopy) {
        let copy = copyObject(this.selectedEntity?.kpis[kpiIndex]);
        copy.employee = employee;
        this.selectedEntity.kpis.push(copy);
      }
  }

  addKpi() {
    if (this.selectedEntity)
      this.selectedEntity.kpis.push({} as Kpi);
  }
}
