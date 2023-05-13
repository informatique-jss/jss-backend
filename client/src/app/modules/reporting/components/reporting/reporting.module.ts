import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { ReportingAddComponent } from '../reporting-add/reporting-add.component';
import { ReportingListComponent } from '../reporting-list/reporting-list.component';
import { ReportingComponent } from './reporting.component';

const routes: Routes = [
  { path: 'reporting/list', component: ReportingListComponent },
  { path: 'reporting/add/:id/:cloning', component: ReportingAddComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatTooltipModule,
    MiscellaneousModule,
    MatIconModule,
    ReactiveFormsModule,
  ],
  declarations: [ReportingComponent,
    ReportingListComponent,
    ReportingAddComponent,
  ],
  exports: [ReportingComponent,
  ],
})
export class ReportingModule { }

