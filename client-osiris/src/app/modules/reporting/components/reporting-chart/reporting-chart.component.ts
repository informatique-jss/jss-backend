import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ColumnDef } from '@tanstack/angular-table';
import { EChartsOption } from 'echarts';
import { provideEchartsCore } from 'ngx-echarts';
import { EchartComponent } from '../../../../libs/echart/echart.component';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { TanstackTableComponent } from '../../../../libs/tanstack-table/tanstack-table.component';
import { ReportingWidget } from '../../model/ReportingWidget';
import { ReportingWidgetService } from '../../services/reporting-widget.service';
import { echarts } from './echarts-config';

@Component({
  selector: 'reporting-chart',
  templateUrl: './reporting-chart.component.html',
  styleUrls: ['./reporting-chart.component.css'],
  standalone: true,
  providers: [provideEchartsCore({ echarts })],
  imports: [...SHARED_IMPORTS, EchartComponent, TanstackTableComponent]
})
export class ReportingChartComponent implements OnInit {

  @Input() widget!: ReportingWidget;
  options: EChartsOption | undefined;
  columns: ColumnDef<any>[] | undefined;
  data: any[] | undefined;
  showLegend = true;
  series: any[] | undefined;

  @ViewChild(TanstackTableComponent) tableChild!: TanstackTableComponent<any>;

  constructor(
    private reportingWidgetService: ReportingWidgetService
  ) { }

  ngOnInit() {
    this.reportingWidgetService.getReportingWidgetPayload(this.widget.id!).subscribe(response => {
      this.showLegend = response.series && response.series.filter((s: { type: string; }) => s.type != "table").length > 1;
      this.series = response ? response.series.filter((s: { type: string; }) => s.type != "table") : [];
      if (response.series.filter((s: { type: string; }) => s.type == "table").length > 0)
        this.initTable(response);
    })
  }

  categoryClicked(category: string) {
    if (this.tableChild)
      this.tableChild.applyExternalFilter(category);
  }

  initTable(response: any) {
    let serie = response.series.filter((s: { type: string; }) => s.type == "table")[0];
    this.data = serie.data;

    this.columns = [];
    for (let column of serie.columns) {
      this.columns.push({
        accessorKey: column,
        header: column,
        enableSorting: true,
        cell: info => info.getValue(),
      })
    }
  }

}
