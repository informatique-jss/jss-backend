import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { ReportingComponent } from './reporting.component';

const routes: Routes = [
  { path: 'reporting', component: ReportingComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  declarations: [ReportingComponent]
})
export class ReportingModule { }
