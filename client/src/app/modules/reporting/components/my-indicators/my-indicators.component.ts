import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { formatDateHourFrance } from 'src/app/libs/FormatHelper';
import { Employee } from 'src/app/modules/profile/model/Employee';
import { EmployeeService } from 'src/app/modules/profile/services/employee.service';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { IndicatorGroup } from '../../model/IndicatorGroup';
import { IndicatorValue } from '../../model/IndicatorValue';
import { Kpi } from '../../model/Kpi';
import { IndicatorGroupService } from '../../services/indicator-group.service';
import { IndicatorValueService } from '../../services/indicator.value.service';

@Component({
  selector: 'my-indicators',
  templateUrl: './my-indicators.component.html',
  styleUrls: ['./my-indicators.component.css']
})
export class MyIndicatorsComponent implements OnInit {

  currentEmployee: Employee | undefined;
  values: IndicatorValue[] = [];
  indicatorGroups: IndicatorGroup[] = [];

  constructor(private appService: AppService,
    private indicatorValueService: IndicatorValueService,
    private employeeService: EmployeeService,
    private habilitationsService: HabilitationsService,
    private formBuilder: FormBuilder,
    private indicatorGroupService: IndicatorGroupService
  ) { }

  formIndicator = this.formBuilder.group({});
  formatDateHourFrance = formatDateHourFrance;

  ngOnInit() {
    this.appService.changeHeaderTitle("Mes indicateurs");
    this.indicatorGroupService.getIndicatorGroups().subscribe(response => { this.indicatorGroups = response.sort((a, b) => a.label.localeCompare(b.label)) })
    this.employeeService.getCurrentEmployee().subscribe(currentEmployee => {
      this.currentEmployee = currentEmployee;
      this.loadValues();
    })
  }

  loadValues() {
    if (this.currentEmployee)
      this.indicatorValueService.getIndicatorValuesForEmployee(this.currentEmployee.id).subscribe(values => {
        this.values = values;
      })
  }

  canDisplayMyIndicatorsForEverybody() {
    return this.habilitationsService.canDisplayMyIndicatorsForEverybody();
  }

  hasKpi(item: IndicatorValue): boolean {
    return item.indicator.kpis?.some(k => k.employee.id === item.employee.id);
  }

  getKpi(item: IndicatorValue): Kpi | null {
    const employeeId = item.employee.id;
    const date = new Date(item.date);
    return item.indicator.kpis
      ?.filter(k => k.employee.id === employeeId && new Date(k.applicationDate) <= date)
      .sort((a, b) => new Date(b.applicationDate).getTime() - new Date(a.applicationDate).getTime())[0] || null;
  }

  displayDetailedIndicator(item: IndicatorValue) {
    this.appService.openRoute(undefined, "indicator/detailed/" + item.indicator.id + "/" + item.employee.id, undefined);
  }

  getIndicatorMetas(item: IndicatorValue) {
    let values = [];
    if (this.getKpi(item)!.minValue)
      values.push('Valeur min : ' + this.getKpi(item)!.minValue);
    if (this.getKpi(item)!.mediumValue)
      values.push('Valeur mediane : ' + this.getKpi(item)!.mediumValue);
    if (this.getKpi(item)!.maxValue)
      values.push('Valeur max : ' + this.getKpi(item)!.maxValue);
    if (this.getKpi(item)!.baseValue)
      values.push('Base de calcul : ' + this.getKpi(item)!.baseValue);
    return values.join(" / ");
  }

  getValueClass(item: IndicatorValue): string {
    const kpi = this.getKpi(item);
    const value = item.value ?? 0;

    if (!kpi) return '';

    const hasMin = kpi.minValue != null;
    const hasMedium = kpi.mediumValue != null;
    const hasMax = kpi.maxValue != null;

    let reverse = item.indicator.isReverse;

    // 1 objective
    if (hasMin && !hasMedium && !hasMax) {
      return reverse
        ? value > kpi.minValue ? 'red' : 'green'
        : value < kpi.minValue ? 'red' : 'green';
    }

    if (!hasMin && hasMedium && !hasMax) {
      return reverse
        ? value > kpi.mediumValue ? 'red' : 'green'
        : value < kpi.mediumValue ? 'red' : 'green';
    }

    if (!hasMin && !hasMedium && hasMax) {
      return reverse
        ? value > kpi.maxValue ? 'red' : 'green'
        : value < kpi.maxValue ? 'red' : 'green';
    }

    // 2 objectives
    if (hasMin && hasMedium && !hasMax) {
      if (!reverse) {
        if (value < kpi.minValue) return 'red';
        if (value < kpi.mediumValue) return 'orange';
        return 'green';
      } else {
        if (value > kpi.mediumValue) return 'red';
        if (value > kpi.minValue) return 'orange';
        return 'green';
      }
    }

    if (hasMin && !hasMedium && hasMax) {
      if (!reverse) {
        if (value < kpi.minValue) return 'red';
        if (value < kpi.maxValue) return 'orange';
        return 'green';
      } else {
        if (value > kpi.maxValue) return 'red';
        if (value > kpi.minValue) return 'orange';
        return 'green';
      }
    }

    if (!hasMin && hasMedium && hasMax) {
      if (!reverse) {
        if (value < kpi.mediumValue) return 'red';
        if (value < kpi.maxValue) return 'orange';
        return 'green';
      } else {
        if (value > kpi.maxValue) return 'red';
        if (value > kpi.mediumValue) return 'orange';
        return 'green';
      }
    }


    // 3 objectives
    if (hasMin && hasMedium && hasMax) {
      if (!reverse) {
        if (value < kpi.minValue) return 'red';
        if (value < kpi.mediumValue) return 'orange';
        if (value < kpi.maxValue) return 'yellow';
        return 'green';
      } else {
        if (value > kpi.maxValue) return 'red';
        if (value > kpi.mediumValue) return 'orange';
        if (value > kpi.minValue) return 'yellow';
        return 'green';
      }
    }

    return '';
  }








}
