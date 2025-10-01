import { Component, EventEmitter, inject, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { EChartsOption } from 'echarts';
import type { EChartsType } from 'echarts/core';
import { NgxEchartsDirective } from 'ngx-echarts';
import { Subscription } from 'rxjs';
import { LayoutStoreService } from '../../modules/main/services/layout-store.service';
import { SHARED_IMPORTS } from '../SharedImports';

@Component({
  selector: 'app-echart',
  templateUrl: './echart.component.html',
  standalone: true,
  imports: [NgxEchartsDirective, ...SHARED_IMPORTS],
})
export class EchartComponent implements OnInit, OnDestroy {
  @Input() options: EChartsOption = {};

  @Input() height: string = '300px';
  @Input() width: string = 'auto';
  @Output() chartInit = new EventEmitter<EChartsType>();

  layout = inject(LayoutStoreService);

  private layoutSub!: Subscription;

  ngOnInit(): void {
    // refresh chart on theme and skin change
    this.layoutSub = this.layout.layoutState$.subscribe(state => {
      const skin = state.skin;
      const theme = state.theme;
    });
  }

  ngOnDestroy(): void {
    this.layoutSub?.unsubscribe();
  }

  onChartInit(instance: EChartsType) {
    this.chartInit.emit(instance);
  }
}
