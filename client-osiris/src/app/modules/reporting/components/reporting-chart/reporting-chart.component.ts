import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ColumnDef } from '@tanstack/angular-table';
import { EChartsOption } from 'echarts';
import { XAXisOption, YAXisOption } from 'echarts/types/dist/shared';
import { provideEchartsCore } from 'ngx-echarts';
import { EchartComponent } from '../../../../libs/apexchart/echart.component';
import { LABEL_TYPE_DATETIME, LABEL_TYPE_NUMERIC } from '../../../../libs/Constants';
import { getColor } from '../../../../libs/inspinia/utils/color-utils';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { ReportingWidget } from '../../model/ReportingWidget';
import { ReportingWidgetService } from '../../services/reporting-widget.service';
import { TanstackTableComponent } from '../tanstack-table/tanstack-table.component';
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

  @ViewChild(TanstackTableComponent) tableChild!: TanstackTableComponent<any>;

  constructor(
    private reportingWidgetService: ReportingWidgetService
  ) { }

  ngOnInit() {
    this.reportingWidgetService.getReportingWidgetPayload(this.widget.id!).subscribe(response => {
      /*   let defaultOptions = {
           chart: {
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
             events: {
               dataPointSelection: (event: any, chartContext: any, config: any) => {
                 if (this.tableChild)
                   this.tableChild.applyExternalFilter(this.options!.xaxis!.categories[config.dataPointIndex]);
               }
             }
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

         } as EChartsOption;*/

      let defaultOptions = <EChartsOption>{
        color: ["#0099C6", "#DC3912", "#FF9900", "#109618", "#990099",
          "#3366CC", "#DD4477", "#66AA00", "#B82E2E", "#316395",
          "#994499", "#22AA99", "#AAAA11", "#6633CC", "#E67300",
          "#8B0707", "#329262", "#5574A6", "#3B3EAC", "#B77322"]
        ,
        textStyle: {
          fontFamily: getComputedStyle(document.body).fontFamily
        }, tooltip: {
          trigger: "axis",
          backgroundColor: getColor("secondary-bg"),
          borderColor: getColor("border-color"),
          textStyle: { color: getColor("light-text-emphasis") },
        },
        legend: {
          show: (response.series && response.series.filter((s: { type: string; }) => s.type != "table").length > 1),
          top: 0,
          left: 'left',
          textStyle: {
            fontSize: 10
          }
        },
        toolbox: {
          show: true,
          feature: {
            top: 0,
            magicType: { type: ['line', 'bar', 'stack'] },
            restore: {},
            saveAsImage: {}
          }
        },
        xAxis: {
          type: "category",
          axisTick: {
            show: true,
            color: "#676b891f"
          },
          axisLine: {
            show: true,
            color: "#676b891f",
          },
          tooltip: { show: true },
          axisLabel: {
            color: getColor("secondary-color"),
            margin: 15,
            rotate: -20,
            overflow: 'truncate',
            width: 50,
            fontSize: 12
          }
        },
        yAxis: {
          axisLine: {
            show: true,
            color: "#676b891f",
          },
          type: "value", splitLine: {
            lineStyle: {
              color: "#676b891f", type: "dashed"
            }
          },
          axisLabel: {
            show: true,
            color: getColor("secondary-color"),
            margin: 15
          },
          axisTick: {
            show: true,
            color: "#676b891f"
          }
        },
        grid: {
          right: 10, left: 10, bottom: 10, top: 40, containLabel: true
        }
      }

      defaultOptions.series = response ? response.series.filter((s: { type: string; }) => s.type != "table") : [];

      if (defaultOptions.series)
        for (let serie of defaultOptions.series as any) {

          serie.selectedMode = 'single';
          serie.emphasis = {
            itemStyle: {
              opacity: 0.6
            }
          },
            serie.select = {
              itemStyle: {
                borderWidth: 2,
                borderColor: "#676b89"
              }
            }

          if ((serie as any).type == "area") {
            serie.type = "line";
            serie.areaStyle = {
              opacity: 0.2, color: getColor("primary")
            };
          }
          if ((serie as any).type == "area" || (serie as any).type == "line") {
            serie.smooth = true;
            serie.symbolSize = 5;
            serie.itemStyle = {
              borderWidth: 2
            };
            serie.lineStyle = {
            };
            serie.symbol = "circle";
          }

        }

      if (this.widget.labelType == LABEL_TYPE_DATETIME) {
        (defaultOptions.xAxis! as XAXisOption).min = undefined;
        (defaultOptions.xAxis! as XAXisOption).max = undefined;
        (defaultOptions.xAxis! as XAXisOption).type = 'time';
        /* (defaultOptions.xAxis! as XAXisOption).labels = {
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
         };*/
      }

      if (this.widget.labelType == LABEL_TYPE_NUMERIC) {
        (defaultOptions.xAxis! as XAXisOption).max = undefined;
        (defaultOptions.xAxis! as XAXisOption).type = "value";
      }

      if (this.widget.lastValueUnit) {
        let unit = this.widget.lastValueUnit;
        (defaultOptions!.yAxis! as YAXisOption).axisLabel = {
          show: true, color: getColor("secondary-color"), margin: 15, formatter: function (value: any) {
            return value + " " + unit;
          }
        }
      }

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
