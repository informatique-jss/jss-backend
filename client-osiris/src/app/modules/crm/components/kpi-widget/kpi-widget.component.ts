import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { NgIcon } from '@ng-icons/core';
import { CountUpModule } from 'ngx-countup';
import { AGGREGATE_TYPE_LAST_VALUE } from '../../../../libs/Constants';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { KpiCrm } from '../../../main/model/KpiCrm';

@Component({
  selector: 'kpi-widget',
  templateUrl: './kpi-widget.component.html',
  styleUrls: ['./kpi-widget.component.css'],
  standalone: true,
  imports: [...SHARED_IMPORTS, CountUpModule, NgIcon]
})
export class KpiWidgetComponent implements OnInit {

  AGGREGATE_TYPE_LAST_VALUE = AGGREGATE_TYPE_LAST_VALUE;

  @Input() kpiCrm: KpiCrm | undefined;
  @Input() value: number = 0;
  @Input() valueN1: number = 0;
  @Input() valueN2: number = 0;
  @Input() evolution: number = 0;
  @Input() evolutionIsGood: boolean = false;
  @Output() onDisplayDetails: EventEmitter<boolean> = new EventEmitter<boolean>();

  constructor() { }

  ngOnInit() {
  }

  displayDetails() {
    this.onDisplayDetails.next(true);
  }
}
