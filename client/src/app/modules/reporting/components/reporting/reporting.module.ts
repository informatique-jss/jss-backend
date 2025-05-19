import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { RouterModule, Routes } from '@angular/router';
import { NgxEchartsModule } from 'ngx-echarts';
import { QuotationModule } from 'src/app/modules/quotation/components/quotation/quotation.module';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { IncidentReportingComponent } from '../incident-reporting/incident-reporting.component';
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
    MatMenuModule,
    MatBadgeModule,
    DragDropModule,
    MatProgressBarModule,
    MatRadioModule,
    QuotationModule,
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
    MyIndicatorsComponent,
    IncidentReportingComponent
  ],
  exports: [ReportingComponent,
  ],
})
export class ReportingModule { }
