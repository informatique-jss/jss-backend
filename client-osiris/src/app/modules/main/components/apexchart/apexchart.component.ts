import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { ApexOptions, ChartComponent } from 'ng-apexcharts';
import { Subscription } from 'rxjs';
import { LayoutStoreService } from '../../services/layout-store.service';

@Component({
  selector: 'app-apexchart',
  templateUrl: './apexchart.component.html',
  standalone: true,
  imports: [ChartComponent],
})
export class ApexchartComponent implements OnInit, OnDestroy {
  @Input() getOptions!: () => ApexOptions

  options: ReturnType<any>

  layout = inject(LayoutStoreService);

  private layoutSub!: Subscription;

  ngOnInit(): void {
    this.options = this.getOptions();

    // refresh chart on theme and skin change
    this.layoutSub = this.layout.layoutState$.subscribe(state => {
      const skin = state.skin;
      const theme = state.theme;
      this.options = this.getOptions();
    });
  }

  ngOnDestroy(): void {
    this.layoutSub?.unsubscribe();
  }
}
