import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PdfToolsComponent } from './pdf-tools/pdf-tools.component';
import { Routes } from '@angular/router';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatIconModule } from '@angular/material/icon';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { SortTableComponent } from '../miscellaneous/components/sort-table/sort-table.component';


const routes: Routes = [
  { path: 'pdf-tools', component: PdfToolsComponent },
];

@NgModule({
  declarations: [],
  imports: [
    CommonModule,
  RouterModule.forChild(routes),
  FormsModule,
  MatButtonModule,
  MatIconModule,
  MatTooltipModule,
  ReactiveFormsModule,
  MatExpansionModule,
  MatCheckboxModule,
  MatFormFieldModule,
  MatInputModule,
  ]
})

export class PdfToolsModule { }
