import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { CountUpModule } from 'ngx-countup';
import { AnalyticStatsType } from '../../../main/model/AnalyticStatsType';

@Component({
  selector: 'app-analytic-statistic-widget',
  imports: [
    NgIcon,
    CountUpModule,
    CommonModule
  ],
  templateUrl: './analytic-statistic-widget.component.html',
  standalone: true,
})
export class AnalyticStatisticWidgetComponent {
  @Input() stat!: AnalyticStatsType;

  @Input() osirisStat!: AnalyticStatsType;
}
