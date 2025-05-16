import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { RouterModule, Routes } from '@angular/router';
import { NgxEchartsModule } from 'ngx-echarts';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { IndicatorDetailedComponent } from '../indicator-detailed/indicator-detailed.component';
import { IndicatorComponent } from '../indicator/indicator.component';
import { MyIndicatorsComponent } from '../my-indicators/my-indicators.component';
import { ReportingAddComponent } from '../reporting-add/reporting-add.component';
import { ReportingListComponent } from '../reporting-list/reporting-list.component';
import { ReportingComponent } from './reporting.component';

const routes: Routes = [
  { path: 'reporting/list', component: ReportingListComponent },
  { path: 'indicator', component: IndicatorComponent },
  { path: 'indicator/detailed/:idIndicator/:idEmployee', component: IndicatorComponent },
  { path: 'reporting/add/:id/:cloning', component: ReportingAddComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatSidenavModule,
    MatListModule,
    MatTableModule,
    FormsModule,
    MatButtonModule,
    MatTooltipModule,
    MatTreeModule,
    MatCheckboxModule,
    MiscellaneousModule,
    MatTabsModule,
    MatIconModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    MatProgressBarModule,
    MatRadioModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts'),
    }),
    ReactiveFormsModule,
  ],
  declarations: [ReportingComponent,
    ReportingListComponent,
    ReportingAddComponent,
    IndicatorDetailedComponent,
    IndicatorComponent,
    MyIndicatorsComponent
  ],
  exports: [ReportingComponent,
  ],
})
export class ReportingModule { }
