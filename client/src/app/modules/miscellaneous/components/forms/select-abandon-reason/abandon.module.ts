import { DragDropModule } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MAT_DATE_LOCALE } from '@angular/material/core';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule } from '@angular/material/dialog';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatRadioModule } from '@angular/material/radio';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSortModule } from '@angular/material/sort';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { SelectAbandonReasonComponent } from './select-abandon-reason';
import { AbandonReasonInquiryDialog } from 'src/app/modules/quotation/components/abandon-reason-inquiry-dialog/abandon-reason-inquiry-dialog';
import { MatSelectModule } from '@angular/material/select';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatChipsModule } from '@angular/material/chips';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatMenuModule } from '@angular/material/menu';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatFormFieldModule } from '@angular/material/form-field';

const routes: Routes = [
  { path: 'abandon-reason', component: SelectAbandonReasonComponent }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MatTabsModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
    MatDialogModule,
    MatTableModule,
    MatIconModule,
    MatExpansionModule,
    MatListModule,
    MatRadioModule,
    MatSortModule,
    MiscellaneousModule,
    MatTooltipModule,
    DragDropModule,
    MatSelectModule,
    MatAutocompleteModule,
    MatChipsModule,
    MatCheckboxModule,
    MatSidenavModule,
    MatMenuModule,
    MatDatepickerModule,
    MatFormFieldModule,
  ],
  declarations: [
    SelectAbandonReasonComponent,
    AbandonReasonInquiryDialog,
  ],
  providers: [
    { provide: MAT_DATE_LOCALE, useValue: 'fr-FR' }
  ],
  exports: [
    SelectAbandonReasonComponent,
    AbandonReasonInquiryDialog,
  ]
})
export class AbandonReasonModule { }
