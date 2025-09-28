import { Component, Input, OnInit } from '@angular/core';
import { ColumnDef } from '@tanstack/angular-table';
import { ApexOptions } from 'ng-apexcharts';
import { LABEL_TYPE_DATETIME, LABEL_TYPE_NUMERIC } from '../../../../libs/Constants';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ApexchartComponent } from '../../../../libs/apexchart/apexchart.component';
import { ReportingWidget } from '../../model/ReportingWidget';
import { ReportingWidgetService } from '../../services/reporting-widget.service';
import { TanstackTableComponent } from '../tanstack-table/tanstack-table.component';

@Component({
  selector: 'reporting-chart',
  templateUrl: './reporting-chart.component.html',
  styleUrls: ['./reporting-chart.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS, ApexchartComponent, TanstackTableComponent]
})
export class ReportingChartComponent implements OnInit {

  @Input() widget!: ReportingWidget;
  options: ApexOptions | undefined;
  columns: ColumnDef<any>[] | undefined;
  data: any[] | undefined;

  constructor(
    private reportingWidgetService: ReportingWidgetService
  ) { }

  ngOnInit() {
    this.reportingWidgetService.getReportingWidgetPayload(this.widget.id!).subscribe(response => {
      let defaultOptions = {
        chart: {
          height: 407,
          fontFamily: "Inter, Roboto, sans-serif",
          foreColor: "#4a4a4a",
          toolbar: {
            export: {
              csv: {
                filename: this.widget.label,
                columnDelimiter: ";",
              }
            }
          },
          zoom: {
            allowMouseWheelZoom: false,
            type: 'x',
            autoScaleYaxis: true
          },
          locales: [
            {
              name: "en",
              options: {
                toolbar: {
                  download: "Télécharger",
                  selection: "Sélectionner",
                  selectionZoom: "Zoom sur sélection",
                  zoomIn: "Zoom +",
                  zoomOut: "Zoom -",
                  pan: "Déplacer",
                  reset: "Réinitialiser le zoom",
                  exportToSVG: "Exporter en SVG",
                  exportToPNG: "Exporter en PNG",
                  exportToCSV: "Exporter en CSV"
                },
                months: [
                  'janvier', 'février', 'mars', 'avril', 'mai', 'juin',
                  'juillet', 'août', 'septembre', 'octobre', 'novembre', 'décembre'
                ],
                shortMonths: ['jan', 'fév', 'mar', 'avr', 'mai', 'jun', 'jul', 'aoû', 'sep', 'oct', 'nov', 'déc'],
                days: ['dimanche', 'lundi', 'mardi', 'mercredi', 'jeudi', 'vendredi', 'samedi'],
              }
            }
          ],
        },
        series: [{}],
        stroke: {
          curve: "smooth",
          width: 2
        },
        markers: {
          size: 3,
          strokeWidth: 1,
          strokeColors: "#fff",
          hover: { size: 5 }
        },
        xaxis: {
          min: 0,
          labels: {
            show: true,
            trim: true,
            rotate: -45,
            maxHeight: 80,
            rotateAlways: false,
            style: { fontSize: "12px" }
          },
          axisBorder: { show: true },
          axisTicks: { show: true },
          categories: []
        },
        yaxis: {
          show: true,
          labels: {
            show: true,
            style: { fontSize: "12px" }
          },
          axisBorder: { show: true },
          axisTicks: { show: true }
        },
        grid: {
          padding: {
            right: 10,
          },
        },
        colors: [
          "#0099C6", "#DC3912", "#FF9900", "#109618", "#990099",
          "#3366CC", "#DD4477", "#66AA00", "#B82E2E", "#316395",
          "#994499", "#22AA99", "#AAAA11", "#6633CC", "#E67300",
          "#8B0707", "#329262", "#5574A6", "#3B3EAC", "#B77322"
        ],
        legend: {
          show: true,
          position: "top",
          horizontalAlign: "left",
          fontSize: "13px",
          fontWeight: 600,
        },
        tooltip: {
          style: { fontSize: "12px" }
        },
        fill: {
          type: "solid"
        },

      } as ApexOptions;

      defaultOptions.series = response ? response.series.filter((s: { type: string; }) => s.type != "table") : [];

      if (defaultOptions.series)
        for (let serie of defaultOptions.series) {
          if ((serie as any).type == "area") {
            defaultOptions.fill!.opacity = 0.3;
          }
        }

      if (this.widget.labelType == LABEL_TYPE_DATETIME) {
        defaultOptions.xaxis!.min = undefined;
        defaultOptions.xaxis!.max = undefined;
        defaultOptions.xaxis!.type = "datetime";
        defaultOptions.xaxis!.labels = {
          datetimeFormatter: {
            year: 'yyyy',
            month: "MMM",
            day: 'dd MMM',
            hour: 'HH:mm',
            minute: 'HH:mm'
          },
          style: {
            fontSize: "12px"
          }
        };
        defaultOptions.tooltip!.x = {
          formatter: (val: number) => {
            return new Intl.DateTimeFormat('fr-FR', {
              weekday: 'long',
              year: 'numeric',
              month: 'long',
              day: '2-digit'
            }).format(new Date(val));
          }
        };
      }

      if (this.widget.labelType == LABEL_TYPE_NUMERIC) {
        defaultOptions.xaxis!.max = undefined;
        defaultOptions.xaxis!.type = "numeric";
      }

      defaultOptions.xaxis!.categories = response ? response.labels : [];
      this.options = defaultOptions;

      if (response.series.filter((s: { type: string; }) => s.type == "table").length > 0)
        this.initTable(response);
    })
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
