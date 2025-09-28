import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { GRAPH_TYPE_AREA, GRAPH_TYPE_BAR, GRAPH_TYPE_LINE, GRAPH_TYPE_TABLE, LABEL_TYPE_CATEGORY, LABEL_TYPE_DATETIME, LABEL_TYPE_NUMERIC } from 'src/app/libs/Constants';
import { ReportingWidget } from 'src/app/modules/reporting/model/ReportingWidget';
import { ReportingWidgetSerie } from 'src/app/modules/reporting/model/ReportingWidgetSerie';
import { ReportingWidgetService } from 'src/app/modules/reporting/services/reporting-widget.service';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-reporting-widget',
  templateUrl: './referential-reporting-widget.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialReportingWidgetComponent extends GenericReferentialComponent<ReportingWidget> implements OnInit {

  labelTypes = [LABEL_TYPE_NUMERIC, LABEL_TYPE_DATETIME, LABEL_TYPE_CATEGORY]
  graphTypes = [GRAPH_TYPE_LINE, GRAPH_TYPE_AREA, GRAPH_TYPE_BAR, GRAPH_TYPE_TABLE]
  deleteIndex = 0;
  GRAPH_TYPE_TABLE = GRAPH_TYPE_TABLE;

  constructor(private reportingWidgetService: ReportingWidgetService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  getAddOrUpdateObservable(): Observable<ReportingWidget> {
    return this.reportingWidgetService.addOrUpdateReportingWidget(this.selectedEntity!);
  }
  getGetObservable(): Observable<ReportingWidget[]> {
    return this.reportingWidgetService.getReportingWidgets();
  }

  deleteSerie(serie: ReportingWidgetSerie) {
    this.deleteIndex++;
    if (this.selectedEntity && this.selectedEntity.reportingWidgetSeries)
      for (let i = 0; i < this.selectedEntity.reportingWidgetSeries.length; i++)
        if (this.selectedEntity.reportingWidgetSeries[i].serieName == serie.serieName)
          this.selectedEntity.reportingWidgetSeries.splice(i, 1);
  }

  addSerie() {
    if (this.selectedEntity)
      if (!this.selectedEntity.reportingWidgetSeries)
        this.selectedEntity.reportingWidgetSeries = [] as Array<ReportingWidgetSerie>;
    this.selectedEntity?.reportingWidgetSeries.push({} as ReportingWidgetSerie);
  }
}
