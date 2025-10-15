import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output, SimpleChanges } from '@angular/core';
import { ECharts, EChartsOption, LegendComponentOption } from 'echarts';
import type { EChartsType } from 'echarts/core';
import { TooltipOption, XAXisOption, YAXisOption } from 'echarts/types/dist/shared';
import { NgxEchartsDirective } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import * as XLSX from "xlsx";
import { GRAPH_TYPE_PIE, LABEL_TYPE_CATEGORY } from '../../../../../client/src/app/libs/Constants';
import { LayoutStoreService } from '../../modules/main/services/layout-store.service';
import { GRAPH_TYPE_AREA, GRAPH_TYPE_BOXPLOT, GRAPH_TYPE_LINE, GRAPH_TYPE_SANKEY, GRAPH_TYPE_TREEMAP, LABEL_TYPE_DATETIME, LABEL_TYPE_NUMERIC } from '../Constants';
import { formatCurrency, formatDate } from '../FormatHelper';
import { getColor } from '../inspinia/utils/color-utils';
import { SHARED_IMPORTS } from '../SharedImports';

@Component({
  selector: 'app-echart',
  templateUrl: './echart.component.html',
  standalone: true,
  imports: [NgxEchartsDirective, ...SHARED_IMPORTS],
})
export class EchartComponent implements OnInit, OnDestroy {
  @Input() height: number = 300;
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
    this.initChart();
  }

  initChart() {
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
      },
      tooltip: {
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
                let yVal = Array.isArray(p.value) ? p.value[1] : p.value;
                if (this.unit == '€')
                  yVal = formatCurrency(yVal);
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

        if ((serie as any).type == GRAPH_TYPE_AREA) {
          serie.type = GRAPH_TYPE_LINE;
          serie.areaStyle = {
            opacity: 0.2, color: getColor("primary")
          };
        }

        if ((serie as any).type == GRAPH_TYPE_PIE || (serie as any).type == GRAPH_TYPE_TREEMAP) {
          serie.radius = ['40%', '70%'];
          serie.emphasis = {
            label: {
              show: true,
              fontSize: 20,
              fontWeight: 'bold'
            }
          };
          defaultOptions.xAxis = {};
          defaultOptions.yAxis = {};
          defaultOptions.xAxis = { show: false };
          defaultOptions.yAxis = { show: false };
          defaultOptions.grid = { show: false };
          (defaultOptions.tooltip! as TooltipOption).trigger = "item";
          serie.data = serie.data.map((item: any[]) => ({
            name: item[0],
            value: item[1]
          }));
        }

        if ((serie as any).type == GRAPH_TYPE_AREA || (serie as any).type == GRAPH_TYPE_LINE) {
          serie.smooth = true;
          serie.symbolSize = 5;
          serie.itemStyle = {
            borderWidth: 2
          };
          serie.lineStyle = {
          };
          serie.symbol = "circle";
        }

        if ((serie as any).type == GRAPH_TYPE_BOXPLOT) {
          defaultOptions.xAxis = {
            type: 'category', data: serie.data.map((d: any) => this.labelType == LABEL_TYPE_DATETIME ? formatDate(new Date(d["label"])) : d["label"])
          };
          defaultOptions.yAxis = { type: 'value' };

          let outData = [];
          for (let d of serie.data) {
            outData.push({ value: [d["min"], d["q1"], d["median"], d["q3"], d["max"]] })
          }
          serie.data = outData;
          this.labelType = LABEL_TYPE_CATEGORY;
          (defaultOptions.tooltip as TooltipOption) = {
            trigger: 'item',
            backgroundColor: '#fff',
            borderColor: '#e7e9eb',
            textStyle: {
              color: '#8a969c',
              fontFamily: '"Open Sans", sans-serif'
            },
            formatter: (param: any) => {
              if (!param || !param.data) return '';

              let [, min, q1, median, q3, max] = param.data.value;
              if (this.unit == '€') {
                min = formatCurrency(min);
                q1 = formatCurrency(q1);
                median = formatCurrency(median);
                q3 = formatCurrency(q3);
                max = formatCurrency(max);
              }


              const label = param.name || '(non défini)';

              return `
              <div style="padding:4px 8px">
                <strong>${label}</strong><br/>
                D1&nbsp;: <b>${min} ${this.unit}</b><br/>
                Q1&nbsp;: <b>${q1} ${this.unit}</b><br/>
                Médiane&nbsp;: <b>${median} ${this.unit}</b><br/>
                Q3&nbsp;: <b>${q3} ${this.unit}</b><br/>
                D9&nbsp;: <b>${max} ${this.unit}</b>
              </div>
            `;
            }
          }
        }

        if ((serie as any).type == GRAPH_TYPE_SANKEY) {

          defaultOptions.xAxis = {};
          defaultOptions.yAxis = {};
          defaultOptions.xAxis = { show: false };
          defaultOptions.yAxis = { show: false };
          defaultOptions.grid = { show: false };
          (defaultOptions.tooltip! as TooltipOption).trigger = "item";
          this.labelType = LABEL_TYPE_CATEGORY;
          const nodeNames = new Set<string>();

          for (const link of serie.data) {
            nodeNames.add(link.source);
            nodeNames.add(link.target);
          }

          serie.links = serie.data;
          serie.data = Array.from(nodeNames).map(name => ({ name }));

          (defaultOptions.tooltip as TooltipOption) = {
            trigger: 'item',
            backgroundColor: '#fff',
            borderColor: '#ccc',
            borderWidth: 1,
            textStyle: {
              color: '#333',
              fontFamily: '"Open Sans", sans-serif'
            },
            formatter: function (params: any) {
              if (params.dataType === 'edge') {
                return `
                <div style="padding:6px 8px">
                  <strong>${params.data.source}</strong> → <strong>${params.data.target}</strong><br/>
                  Valeur : <strong>${params.data.value} </strong>
                </div>
              `;
              } else if (params.dataType === 'node') {
                return `
              <div style="padding:6px 8px">
                <strong>${params.data.name}</strong>
                 Valeur : <strong>${params.data.value} </strong>
              </div>
            `;
              }
              return '';
            }
          }
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

    // Handle dimensions
    let computedSeries = [];
    for (let serie of this.series!) {
      let i = 0;
      const hasGroup = serie.data[0][2] != null;

      if (hasGroup && (serie as any).type != GRAPH_TYPE_BOXPLOT && (serie as any).type != GRAPH_TYPE_SANKEY) {
        const grouped = new Map<string, [string, number][]>();

        for (const [categorie, value, group] of serie.data) {
          if (!grouped.has(group)) grouped.set(group, []);
          grouped.get(group)!.push([categorie, value]);
        }

        const newSeries = Array.from(grouped.entries()).map(([name, values]) => ({
          ...structuredClone(serie),
          name,
          data: values,
          stack: 'total' + i
        }));

        i++;
        computedSeries.push(...newSeries);

      } else
        computedSeries.push(serie);
    }


    if (computedSeries.length == 0)
      computedSeries = this.series!;

    defaultOptions.series = computedSeries;
    (defaultOptions.legend! as LegendComponentOption).show = (defaultOptions.series && defaultOptions.series.length > 1 && defaultOptions.series.length < 6);


    this.options = defaultOptions;
  }
  ngOnChanges(changes: SimpleChanges) {
    if (changes['series']) {
      // var a changé — même si le nom est le même, si la référence change, ça détecte
      console.log('Nouvelle valeur de series:', this.series);
      this.initChart();
    }
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
      (s.data as any[]).forEach((d) => {
        if (Array.isArray(d)) {
          categories.add(d[0]);
        } else if (d && typeof d === 'object' && 'name' in d) {
          categories.add(d.name);
        }
      });
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
      const row: any[] = [
        this.labelType == LABEL_TYPE_DATETIME ? new Date(cat) : cat
      ];

      for (const s of series) {
        const dataArray = s.data as any[];
        const found = dataArray.find((d) =>
          Array.isArray(d)
            ? d[0] === cat
            : d && typeof d === 'object' && d.name === cat
        );

        if (found) {
          const value = Array.isArray(found) ? found[1] : found.value;
          row.push(value);
        } else {
          row.push(null);
        }
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
