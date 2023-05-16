import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PdfToolsComponent } from './pdf-tools.component';
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
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTableModule } from '@angular/material/table';
import { MatMenuModule } from '@angular/material/menu';
import { MatListModule } from '@angular/material/list';
import { MatSortModule } from '@angular/material/sort';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatSelectModule } from '@angular/material/select';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatTableExporterModule } from 'mat-table-exporter';
import { MatRadioModule } from '@angular/material/radio';
import { MatCardModule } from '@angular/material/card';
import { MatBadgeModule } from '@angular/material/badge';
import { MatTabsModule } from '@angular/material/tabs';
import { NgxEchartsModule } from 'ngx-echarts';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MiscellaneousModule } from "../../miscellaneous/components/miscellaneous/miscellaneous.module";
import { CompressedComponent } from '../compressed/compressed/compressed.component';

const routes: Routes = [
  { path: 'pdf-tools', component: PdfToolsComponent },
  { path: 'pdf-tools/attachment/upload', component: CompressedComponent },

];

@NgModule({
    declarations: [
        PdfToolsComponent,
    ],
    exports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule
    ],
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
        MatProgressBarModule,
        MatDialogModule,
        MatTableModule,
        MatMenuModule,
        MatListModule,
        MatSortModule,
        DragDropModule,
        MatSelectModule,
        MatChipsModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatTableExporterModule,
        MatRadioModule,
        MatCardModule,
        MatBadgeModule,
        MatTabsModule,
        MatSlideToggleModule,
        MatAutocompleteModule,
        MatProgressSpinnerModule,
        NgxEchartsModule.forRoot({
            echarts: () => import('echarts'),
        }),
        MiscellaneousModule
    ]
})

export class PdfToolsModule { }
