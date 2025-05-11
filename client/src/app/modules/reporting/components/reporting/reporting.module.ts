import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatTreeModule } from '@angular/material/tree';
import { RouterModule, Routes } from '@angular/router';
import { NgxEchartsModule } from 'ngx-echarts';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { IndicatorComponent } from '../indicator/indicator.component';
import { ReportingAddComponent } from '../reporting-add/reporting-add.component';
import { ReportingListComponent } from '../reporting-list/reporting-list.component';
import { ReportingComponent } from './reporting.component';

const routes: Routes = [
  { path: 'reporting/list', component: ReportingListComponent },
  { path: 'indicator', component: IndicatorComponent },
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
    MatIconModule,
    MatRadioModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts'),
    }),
    ReactiveFormsModule,
  ],
  declarations: [ReportingComponent,
    ReportingListComponent,
    ReportingAddComponent,
    IndicatorComponent
  ],
  exports: [ReportingComponent,
  ],
})
export class ReportingModule { }

