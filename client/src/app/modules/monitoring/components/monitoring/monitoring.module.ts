import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { BatchListComponent } from '../batch-list/batch-list.component';
import { MonitoringDetailsComponent } from '../monitoring-details/monitoring-details.component';
import { MonitoringNodesComponent } from '../monitoring-nodes/monitoring-nodes.component';
import { MonitoringSummaryComponent } from '../monitoring-summary/monitoring-summary.component';
import { MonitoringComponent } from './monitoring.component';

const routes: Routes = [
  { path: 'monitoring', component: MonitoringComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatDividerModule,
    MatTabsModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule,
    MiscellaneousModule,
    MatTooltipModule,
    DragDropModule,
    MatMenuModule,
  ], declarations: [
    MonitoringComponent,
    MonitoringSummaryComponent,
    MonitoringDetailsComponent,
    BatchListComponent,
    MonitoringNodesComponent
  ]
})
export class MonitoringModule { }
