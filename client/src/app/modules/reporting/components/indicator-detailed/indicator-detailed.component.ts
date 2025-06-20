import { NestedTreeControl } from '@angular/cdk/tree';
import { formatDate } from '@angular/common';
import { Component, Input, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatTreeNestedDataSource } from '@angular/material/tree';
import { EChartsOption, SeriesOption } from 'echarts';
import { formatDateForSortTable, formatDateFrance } from 'src/app/libs/FormatHelper';
import { ActiveDirectoryGroup } from 'src/app/modules/miscellaneous/model/ActiveDirectoryGroup';
import { SortTableColumn } from 'src/app/modules/miscellaneous/model/SortTableColumn';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { ActiveDirectoryGroupService } from '../../../miscellaneous/services/active.directory.group.service';
import { Employee } from '../../../profile/model/Employee';
import { EmployeeNode } from '../../model/EmployeeNode';
import { Indicator } from '../../model/Indicator';
import { IndicatorValue } from '../../model/IndicatorValue';
import { Kpi } from '../../model/Kpi';
import { IndicatorService } from '../../services/indicator.service';
import { IndicatorValueService } from '../../services/indicator.value.service';

@Component({
  selector: 'indicator-detailed',
  templateUrl: './indicator-detailed.component.html',
  styleUrls: ['./indicator-detailed.component.css']
})
export class IndicatorDetailedComponent implements OnInit {

  @Input() idIndicator: number | undefined;
  @Input() idEmployee: number | undefined;


  indicatorValues: IndicatorValue[] | undefined;
  allEmployees: Employee[] = [];
  employees: Employee[] = [];
  chartOptions: EChartsOption = {};
  filteredValues: IndicatorValue[] = [];
  mode: 'monthly' | 'cumulative' = 'monthly';
  selectedIndicator: Indicator | undefined;

  treeControl = new NestedTreeControl<EmployeeNode>(node => node.children);
  dataSource = new MatTreeNestedDataSource<EmployeeNode>();
  selectedEmployeeIds = new Set<number>();
  activeDirectoryGroups: ActiveDirectoryGroup[] = [];
  displayedColumns: SortTableColumn<IndicatorValue>[] = [];
  currentColors: string[] | undefined;

  constructor(
    private appService: AppService,
    private indicatorValueService: IndicatorValueService,
    private employeeService: EmployeeService,
    private indicatorService: IndicatorService,
    private activeDirectoryGroupService: ActiveDirectoryGroupService,
    private formBuilder: FormBuilder
  ) { }

  indicatorForm = this.formBuilder.group({});

  ngOnInit() {
    this.appService.changeHeaderTitle("Indicateurs");

    this.activeDirectoryGroupService.getActiveDirectoryGroups().subscribe(response => {
      this.activeDirectoryGroups = response;
    })

    this.employeeService.getEmployees().subscribe(response => {
      this.allEmployees = response;
      if (this.idEmployee) {
        for (let employee of this.allEmployees) {
          if (employee.id == this.idEmployee)
            this.selectedEmployeeIds.add(employee.id);
        }
        if (this.idIndicator) {
          this.indicatorService.getIndicator(this.idIndicator).subscribe(response => {
            this.selectedIndicator = response;
            this.refreshIndicator(true);
          });

        }
      }
    })

    this.displayedColumns = [];
    this.displayedColumns.push({ id: "employee", fieldName: "employee", label: "Collaborateur", displayAsEmployee: true } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "value", fieldName: "value", label: "Mesure" } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "date", fieldName: "date", label: "Date de mesure", valueFonction: formatDateForSortTable } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "minValue", fieldName: "minValue", label: "Valeur minimale", valueFonction: (element: IndicatorValue, column: SortTableColumn<IndicatorValue>) => { return this.getAllValuesForIndicatorValue(element).minValue } } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "baseValue", fieldName: "baseValue", label: "Base de calcul", valueFonction: (element: IndicatorValue, column: SortTableColumn<IndicatorValue>) => { return this.getAllValuesForIndicatorValue(element).baseValue } } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "maxValue", fieldName: "maxValue", label: "Valeur maximale", valueFonction: (element: IndicatorValue, column: SortTableColumn<IndicatorValue>) => { return this.getAllValuesForIndicatorValue(element).maxValue } } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "mediumValue", fieldName: "mediumValue", label: "Valeur mediane", valueFonction: (element: IndicatorValue, column: SortTableColumn<IndicatorValue>) => { return this.getAllValuesForIndicatorValue(element).mediumValue } } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "succededValue", fieldName: "succededValue", label: "Valeur atteinte" } as SortTableColumn<IndicatorValue>);
    this.displayedColumns.push({ id: "succededPercentage", fieldName: "succededPercentage", label: "% atteint", valueFonction: (element: IndicatorValue, column: SortTableColumn<IndicatorValue>) => { return element.succededPercentage ? Math.round(element.succededPercentage * 100) + '' : '' } } as SortTableColumn<IndicatorValue>);

  }

  getAllValuesForIndicatorValue(indicatorValue: IndicatorValue): Kpi {
    let kpi = this.getKpiForEmployeeAndDate(indicatorValue.employee.id, indicatorValue.date);
    if (kpi)
      return kpi;
    return { baseValue: 0, maxValue: 0, minValue: 0, mediumValue: 0 } as Kpi;
  }

  getKpiForEmployeeAndDate(employeeId: number, date: Date): Kpi | undefined {
    const targetTime = (new Date(date)).getTime();

    return this.selectedIndicator!.kpis
      .filter(k => k.employee.id === employeeId && new Date(k.applicationDate).getTime() <= targetTime)
      .sort((a, b) => new Date(b.applicationDate).getTime() - new Date(a.applicationDate).getTime())
    [0];
  }

  ngOnChanges(): void {
    this.updateData();
  }

  onEmployeeChange(): void {
    this.currentColors = undefined;
    this.updateData();
  }


  refreshIndicator(isRefreshData: boolean = false) {
    if (this.selectedIndicator) {
      this.indicatorValues = [];
      this.employees = [];
      this.dataSource = new MatTreeNestedDataSource<EmployeeNode>();
      this.indicatorValueService.getIndicatorValues(this.selectedIndicator.id!).subscribe(response => {
        if (response) {
          this.indicatorValues = response;
          this.employees = this.allEmployees.filter(e => e.isActive && this.indicatorValues!.map(emp => emp.employee && emp.employee.id ? emp.employee.id : 0).indexOf(e.id) >= 0);
          this.dataSource.data = this.buildEmployeeTree(this.employees);
          if (isRefreshData)
            this.updateData();
        }
      })
    }
  }

  /**
   * Echart management
   */

  updateData(): void {
    if (!this.indicatorValues)
      return;

    const selected = new Set(this.selectedEmployeeIds);
    const showAll = selected.has(-1);

    this.filteredValues = this.indicatorValues.filter(v => showAll || v.employee && selected.has(v.employee.id));

    const grouped: Record<number, IndicatorValue[]> = this.filteredValues.reduce((acc, val) => {
      const id = val.employee.id;
      if (!acc[id]) acc[id] = [];
      acc[id].push(val);
      return acc;
    }, {} as Record<number, IndicatorValue[]>);


    const series: SeriesOption[] = Object.entries(grouped).map(([id, values]: [string, IndicatorValue[]]) => {
      const sortedValues = values
        .slice()
        .sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

      let data: [any, number][] = [];

      if (this.mode === 'cumulative') {
        // Group by year
        const byYear: Record<string, IndicatorValue[]> = {};
        for (const v of sortedValues) {
          const year = new Date(v.date).getFullYear();
          if (!byYear[year]) byYear[year] = [];
          byYear[year].push(v);
        }

        Object.keys(byYear)
          .sort()
          .forEach(year => {
            let sum = 0;
            const yearValues = byYear[year].sort((a, b) => new Date(a.date).getTime() - new Date(b.date).getTime());

            yearValues.forEach(v => {
              sum += v.value;
            });

            // Add cumulative data on last year date
            const lastDate = new Date(yearValues[yearValues.length - 1].date).getFullYear() + '';
            data.push([lastDate, sum]);
          });
      } else {
        data = sortedValues.map(v => [v.date, v.value]);
      }

      return {
        name: this.employees.find(e => e.id === +id)?.firstname + ' ' + this.employees.find(e => e.id === +id)?.lastname,
        type: this.mode === 'cumulative' ? 'bar' : 'line',
        data,
        smooth: true,
        emphasis: { focus: 'series' }
      };
    });

    const isSingleEmployee = this.selectedEmployeeIds.size === 1;
    const kpiSeries = isSingleEmployee && this.mode == 'monthly' ? this.generateKpiSeries(this.selectedIndicator!.kpis.filter(k => k.employee.id === [... this.selectedEmployeeIds][0])) : [];
    const safeKpiSeries = Array.isArray(kpiSeries) ? kpiSeries : kpiSeries ? [kpiSeries] : [];

    this.chartOptions = {
      color: this.currentColors ? this.currentColors : this.generateColors(series.length),
      tooltip: {
        trigger: 'axis',
        formatter: function (params: any) {
          // Sort legend by value desc
          const sorted = [...params].sort((a, b) => b.data[1] - a.data[1]);
          const dateLabel = sorted[0].axisValueLabel;
          const lines = sorted.map(p => `${p.marker} ${p.seriesName}: ${p.data[1]}`);
          return `<strong>${dateLabel}</strong><br/>` + lines.join('<br/>');
        }
      },
      xAxis: {
        type: this.mode === 'cumulative' ? 'category' : 'time' as any,
        axisLabel: {
          formatter: (value: any) => formatDateFrance(value) // optionnel
        }
      },
      yAxis: { type: 'value' },
      legend: { data: series.map(s => s.name as string) },
      series: [...series, ...safeKpiSeries],
      toolbox: {
        feature: {
          saveAsImage: {
            show: true,
            name: this.selectedIndicator!.label
          }
        }
      },
      dataZoom: [
        {
          type: 'slider',
          throttle: 500,
          labelFormatter: function (value, valueStr) {
            let currentDate = value ? formatDate(value, 'dd/MM/yyyy HH:mm', 'fr-FR') : '';
            return currentDate;
          }
        }
      ],
    };
  }

  generateColors(total: number): string[] {
    const themes = [
      { name: 'blue', min: [0, 49, 158], max: [255, 128, 128] },
      { name: 'orange', min: [255, 102, 0], max: [165, 108, 255] },
      { name: 'green', min: [0, 179, 188], max: [234, 217, 0] }
    ];

    const colors: string[] = [];
    const variationsPerTheme = Math.ceil(total / themes.length);

    for (let i = 0; i < total; i++) {
      const themeIndex = Math.floor(Math.random() * 3);
      const theme = themes[themeIndex];

      const variationIndex = Math.floor(i / themes.length);
      const t = variationsPerTheme <= 1 ? 0 : variationIndex / (variationsPerTheme - 1);

      const interpolate = (min: number, max: number) =>
        Math.round(min + t * (max - min));

      const r = interpolate(theme.min[0], theme.max[0]);
      const g = interpolate(theme.min[1], theme.max[1]);
      const b = interpolate(theme.min[2], theme.max[2]);

      colors.push(`rgb(${r}, ${g}, ${b})`);
    }
    this.currentColors = colors;
    return colors;
  }

  generateKpiSeries(kpis: Kpi[]): EChartsOption['series'] | undefined {
    const sorted = [...kpis].sort((a, b) => new Date(a.applicationDate).getTime() - new Date(b.applicationDate).getTime());

    const kpiSeries = ['minValue', 'mediumValue', 'maxValue', 'baseValue'].map(key => {
      const colorMap = {
        minValue: 'orange',
        mediumValue: 'gold',
        maxValue: 'green',
        baseValue: 'gray'
      };

      const data: [Date, number][] = [];

      for (let i = 0; i < sorted.length; i++) {
        const kpi = sorted[i];
        const value = kpi[key as keyof Kpi];
        const date = new Date(kpi.applicationDate);

        if (value) {
          data.push([date, value as number]);

          // Repeat value until next date
          const nextDate = sorted[i + 1]?.applicationDate;
          if (nextDate) {
            const repeatDate = new Date(nextDate);
            repeatDate.setDate(repeatDate.getDate() - 1);
            const repeatDateStr = repeatDate;
            data.push([repeatDateStr, value as number]);
          }
        }
      }

      // repeat the last one with current date
      data.push([new Date(), sorted[sorted.length - 1][key as keyof Kpi] as number]);

      let name = '';
      if (key == 'minValue')
        name = 'Valeur minimale';
      if (key == 'mediumValue')
        name = 'Valeur mediane';
      if (key == 'maxValue')
        name = 'Valeur maximale';
      if (key == 'baseValue')
        name = 'Base de calcul';

      return {
        name: name,
        type: 'line',
        step: 'end',
        showSymbol: false,
        smooth: false,
        lineStyle: {
          width: 2,
          color: colorMap[key as keyof typeof colorMap]
        },
        data
      };
    }) as SeriesOption[];

    return kpiSeries.filter(s => (s.data as [string, number][]).length > 0);
  }

  /**
   * Tree management
   */

  buildEmployeeTree(employees: Employee[]): EmployeeNode[] {
    const root: Record<string, EmployeeNode> = {};

    for (const emp of employees) {
      const part = this.activeDirectoryGroups.find(a => emp.adPath.indexOf(a.activeDirectoryPath) >= 0)?.label;
      let current = root;
      let parent: EmployeeNode | undefined = undefined;

      if (part) {
        if (!current[part]) {
          current[part] = { name: part, children: [], parent };
        }
        parent = current[part];
        current = current[part].children!.reduce((acc, node) => {
          acc[node.name] = node;
          return acc;
        }, {} as Record<string, EmployeeNode>);

        const childNode: EmployeeNode = { name: emp.firstname, employee: emp, parent };
        parent!.children!.push(childNode);
      }
    }

    return Object.values(root);
  }

  hasChild = (_: number, node: EmployeeNode) => !!node.children && node.children.length > 0;

  toggleSelection(node: EmployeeNode): void {
    const selected = this.isSelected(node);

    if (node.children?.length) {
      this.setSelectedRecursive(node, !selected);
    } else if (node.employee) {
      if (selected) this.selectedEmployeeIds.delete(node.employee.id);
      else this.selectedEmployeeIds.add(node.employee.id);
    }
    this.onEmployeeChange();
  }

  setSelectedRecursive(node: EmployeeNode, select: boolean) {
    if (node.employee) {
      if (select) this.selectedEmployeeIds.add(node.employee.id);
      else this.selectedEmployeeIds.delete(node.employee.id);
    }
    node.children?.forEach(child => this.setSelectedRecursive(child, select));
  }

  isSelected(node: EmployeeNode): boolean {
    if (node.children?.length) {
      return this.getAllLeafEmployeeIds(node).every(id => this.selectedEmployeeIds.has(id));
    } else if (node.employee) {
      return this.selectedEmployeeIds.has(node.employee.id);
    }
    return false;
  }

  isIndeterminate(node: EmployeeNode): boolean {
    if (!node.children?.length) return false;
    const ids = this.getAllLeafEmployeeIds(node);
    const selectedCount = ids.filter(id => this.selectedEmployeeIds.has(id)).length;
    return selectedCount > 0 && selectedCount < ids.length;
  }

  getAllLeafEmployeeIds(node: EmployeeNode): number[] {
    const result: number[] = [];
    if (node.employee) result.push(node.employee.id);
    node.children?.forEach(child => result.push(...this.getAllLeafEmployeeIds(child)));
    return result;
  }

}
