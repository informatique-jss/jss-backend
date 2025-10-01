import { Component, Input, OnInit, ViewChild } from '@angular/core';
import { ColumnDef } from '@tanstack/angular-table';
import { ECharts, EChartsOption } from 'echarts';
import type { EChartsType } from 'echarts/core';
import { XAXisOption, YAXisOption } from 'echarts/types/dist/shared';
import { provideEchartsCore } from 'ngx-echarts';
import * as XLSX from 'xlsx';
import { LABEL_TYPE_DATETIME, LABEL_TYPE_NUMERIC } from '../../../../libs/Constants';
import { EchartComponent } from '../../../../libs/echart/echart.component';
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
      let defaultOptions = <EChartsOption>{
        color: ["#0099C6", "#DC3912", "#FF9900", "#109618", "#990099",
          "#3366CC", "#DD4477", "#66AA00", "#B82E2E", "#316395",
          "#994499", "#22AA99", "#AAAA11", "#6633CC", "#E67300",
          "#8B0707", "#329262", "#5574A6", "#3B3EAC", "#B77322"]
        ,
        locale: 'FR',
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
        dataZoom: [
          {
            type: 'inside',
            xAxisIndex: 0,
            zoomOnMouseWheel: false,
            moveOnMouseMove: true,
          },
        ],
        toolbox: {
          show: true,
          feature: {
            top: 0,
            magicType: {
              type: ['line', 'bar', 'stack'],
            },
            restore: {
            },
            saveAsImage: {
            },
            dataZoom: {
              yAxisIndex: false,
              title: {
                zoom: 'Zoom sur sélection',
                back: 'Réinitialiser le zoom'
              }
            },
            myTool1: {
              show: true,
              title: 'Exporter en Excel',
              icon: 'path://M6 4 L10 4 L14 10 L18 4 L22 4 L16 12 L22 20 L18 20 L14 14 L10 20 L6 20 L12 12 Z',
              iconStyle: {
                color: '#217346',
                borderColor: '#217346'
              },
              onclick: (ec: ECharts) => this.exportExcel(ec)
            }
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

  listenChartClick(ec: EChartsType) {
    ec.on('click', (params: any) => {
      if (params.componentType === 'series') {
        const clickedData = params.data;

        if (this.tableChild) {
          this.tableChild.applyExternalFilter(clickedData[0]);
        }
      }
    });
  }

  exportExcel(ec: ECharts) {
    if (!this.options)
      return;

    const series = this.options.series as any[];
    const categories = new Set<string>();
    for (const s of series) {
      (s.data as [string, number][]).forEach(([x]) => categories.add(x));
    }
    const catArray = Array.from(categories);

    let colonneName = "Catégorie";
    if (this.widget.labelType == LABEL_TYPE_DATETIME)
      colonneName = "Date";

    if (this.widget.labelType == LABEL_TYPE_NUMERIC)
      colonneName = "Abscisse";

    const header = [colonneName, ...series.map((s) => s.name)];
    const rows: any[] = [header];

    for (const cat of catArray) {
      const row: any[] = [this.widget.labelType == LABEL_TYPE_DATETIME ? new Date(cat) : cat];
      for (const s of series) {
        const found = (s.data as [string, number][]).find(([x]) => x === cat);
        row.push(found ? found[1] : null);
      }
      rows.push(row);
    }

    const ws: XLSX.WorkSheet = XLSX.utils.aoa_to_sheet(rows);

    // Autofilter & autosize
    const range = XLSX.utils.decode_range(ws['!ref']!);
    ws['!autofilter'] = { ref: XLSX.utils.encode_range(range) };

    let colWidths = rows[0].map((_: any, colIndex: any) => {
      let maxLen = 10;
      rows.forEach(r => {
        const val = r[colIndex] ? r[colIndex].toString() : '';
        maxLen = Math.max(maxLen, val.length);
      });
      return { wch: maxLen + 2 };
    });

    if (this.widget.labelType == LABEL_TYPE_DATETIME)
      colWidths[0] = { wch: 11 };

    ws['!cols'] = colWidths;

    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Données');
    XLSX.writeFile(wb, this.widget.label + '.xlsx');
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
