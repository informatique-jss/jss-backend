import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { GRAPH_TYPE_AREA, GRAPH_TYPE_BAR, GRAPH_TYPE_BOXPLOT, GRAPH_TYPE_LINE, GRAPH_TYPE_PIE, GRAPH_TYPE_SANKEY, GRAPH_TYPE_TABLE, GRAPH_TYPE_TREEMAP, LABEL_TYPE_CATEGORY, LABEL_TYPE_DATETIME, LABEL_TYPE_NUMERIC } from 'src/app/libs/Constants';
import { BatchService } from 'src/app/modules/monitoring/services/batch.service';
import { ReportingWidget } from 'src/app/modules/reporting/model/ReportingWidget';
import { ReportingWidgetSerie } from 'src/app/modules/reporting/model/ReportingWidgetSerie';
import { ReportingWidgetService } from 'src/app/modules/reporting/services/reporting-widget.service';
import { AppService } from 'src/app/services/app.service';
import { BatchSettingsService } from '../../../../monitoring/services/batch.settings.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-reporting-widget',
  templateUrl: './referential-reporting-widget.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialReportingWidgetComponent extends GenericReferentialComponent<ReportingWidget> implements OnInit {

  labelTypes = [LABEL_TYPE_NUMERIC, LABEL_TYPE_DATETIME, LABEL_TYPE_CATEGORY]
  graphTypes = [GRAPH_TYPE_LINE, GRAPH_TYPE_AREA, GRAPH_TYPE_BAR, GRAPH_TYPE_TABLE, GRAPH_TYPE_PIE, GRAPH_TYPE_BOXPLOT, GRAPH_TYPE_TREEMAP, GRAPH_TYPE_SANKEY]
  deleteIndex = 0;
  GRAPH_TYPE_TABLE = GRAPH_TYPE_TABLE;

  constructor(private reportingWidgetService: ReportingWidgetService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,
    private batchService: BatchService,
    private batchSettingsService: BatchSettingsService
  ) {
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

  refreshWidget() {
    this.batchSettingsService.getBatchSettings().subscribe(batchs => {
      for (let batch of batchs)
        if (batch.code == "COMPUTE_REPORTING_WIDGET")
          this.batchService.declareNewBatch(batch, this.selectedEntity!.id!);
    })
  }
}
