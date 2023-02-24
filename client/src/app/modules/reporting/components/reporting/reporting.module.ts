import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
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
  ]
})
export class ReportingModule { }

