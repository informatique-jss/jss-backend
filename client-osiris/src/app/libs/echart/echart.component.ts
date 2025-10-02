import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { ECharts, EChartsOption } from 'echarts';
import type { EChartsType } from 'echarts/core';
import { XAXisOption, YAXisOption } from 'echarts/types/dist/shared';
import { NgxEchartsDirective } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import * as XLSX from "xlsx";
import { LABEL_TYPE_CATEGORY } from '../../../../../client/src/app/libs/Constants';
import { LayoutStoreService } from '../../modules/main/services/layout-store.service';
import { LABEL_TYPE_DATETIME, LABEL_TYPE_NUMERIC } from '../Constants';
import { getColor } from '../inspinia/utils/color-utils';
import { SHARED_IMPORTS } from '../SharedImports';

@Component({
  selector: 'app-echart',
  templateUrl: './echart.component.html',
  standalone: true,
  imports: [NgxEchartsDirective, ...SHARED_IMPORTS],
})
export class EchartComponent implements OnInit, OnDestroy {
  @Input() height: string = '300px';
  @Input() width: string = 'auto';
  @Input() series: any[] | undefined;
  @Input() labelType: string = LABEL_TYPE_CATEGORY;
  @Input() title: string = "";
  @Input() unit: string | undefined;
  @Output() chartInit = new EventEmitter<EChartsType>();
  @Output() categoryClicked = new EventEmitter<string>();
  options: EChartsOption = {};

  layout = inject(LayoutStoreService);

  private layoutSub!: Subscription;

  ngOnInit(): void {
    // refresh chart on theme and skin change
    this.layoutSub = this.layout.layoutState$.subscribe(state => {
      const skin = state.skin;
      const theme = state.theme;
    });

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
        formatter: (params: any) => {
          const isArray = Array.isArray(params);
          const p0 = isArray ? params[0] : params;

          const xLabel = p0.axisValueLabel ?? (Array.isArray(p0.value) ? p0.value[0] : p0.value);

          if (this.labelType !== LABEL_TYPE_DATETIME) {
            const seriesHtml = (isArray ? params : [params])
              .map(p => {
                const yVal = Array.isArray(p.value) ? p.value[1] : p.value;
                return `${p.marker} ${p.seriesName}: ${yVal}`;
              })
              .join('<br/>');

            return `<div><b>${xLabel}</b></div>${seriesHtml}`;
          }

          const date = new Date(p0.value[0]);
          const dateLabel = new Intl.DateTimeFormat('fr-FR', {
            weekday: 'long',
            year: 'numeric',
            month: 'long',
            day: '2-digit'
          }).format(date);

          let html = `<div><b>${dateLabel}</b></div>`;
          (isArray ? params : [params]).forEach(s => {
            html += `<div>${s.marker} ${s.seriesName}: ${s.value[1]}</div>`;
          });

          return html;
        }
      },
      legend: {
        show: (this.series && this.series.length > 1),
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

    if (this.series)
      for (let serie of this.series as any) {

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



    if (this.labelType == LABEL_TYPE_NUMERIC) {
      (defaultOptions.xAxis! as XAXisOption).max = undefined;
      (defaultOptions.xAxis! as XAXisOption).type = "value";
    }

    if (this.labelType == LABEL_TYPE_DATETIME) {
      (defaultOptions.xAxis! as XAXisOption).min = undefined;
      (defaultOptions.xAxis! as XAXisOption).max = undefined;
      (defaultOptions.xAxis! as XAXisOption).type = 'time';
    }

    if (this.unit) {
      let unit = this.unit;
      (defaultOptions!.yAxis! as YAXisOption).axisLabel = {
        show: true, color: getColor("secondary-color"), margin: 15, formatter: function (value: any) {
          return value + " " + unit;
        }
      }
    }

    defaultOptions.series = this.series;
    this.options = defaultOptions;
  }

  listenChartClick(ec: EChartsType) {
    ec.on('click', (params: any) => {
      if (params.componentType === 'series') {
        const clickedData = params.data;

        this.categoryClicked.next(clickedData[0]);
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
    if (this.labelType == LABEL_TYPE_DATETIME)
      colonneName = "Date";

    if (this.labelType == LABEL_TYPE_NUMERIC)
      colonneName = "Abscisse";

    const header = [colonneName, ...series.map((s) => s.name)];
    const rows: any[] = [header];

    for (const cat of catArray) {
      const row: any[] = [this.labelType == LABEL_TYPE_DATETIME ? new Date(cat) : cat];
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

    if (this.labelType == LABEL_TYPE_DATETIME)
      colWidths[0] = { wch: 11 };

    ws['!cols'] = colWidths;

    const wb: XLSX.WorkBook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(wb, ws, 'Données');
    XLSX.writeFile(wb, this.title + '.xlsx');
  }

  ngOnDestroy(): void {
    this.layoutSub?.unsubscribe();
  }

  onChartInit(instance: EChartsType) {
    this.chartInit.emit(instance);
  }
}
