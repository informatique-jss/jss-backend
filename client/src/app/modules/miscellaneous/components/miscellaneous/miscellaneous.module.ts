import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatRadioModule } from '@angular/material/radio';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AddressingComponent } from '../addressing/addressing.component';
import { AttachmentsComponent } from '../attachments/attachments.component';
import { ConfirmDialogComponent } from '../confirm-dialog/confirm-dialog.component';
import { AutocompleteCityComponent } from '../forms/autocomplete-city/autocomplete-city.component';
import { GenericDatepickerComponent } from '../forms/generic-datepicker/generic-datepicker.component';
import { GenericInputComponent } from '../forms/generic-input/generic-input.component';
import { GenericToggleComponent } from '../forms/generic-toggle/generic-toggle.component';
import { RadioGroupCivilityComponent } from '../forms/radio-group-civility/radio-group-civility.component';
import { SelectTiersCategoryComponent } from '../forms/select-tiers-category/select-tiers-category.component';
import { SelectTiersTypeComponent } from '../forms/select-tiers-type/select-tiers-type.component';
import { HistoryComponent } from '../history/history.component';
import { SingleAttachmentComponent } from '../single-attachment/single-attachment.component';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';
import { MiscellaneousComponent } from './miscellaneous.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatTableModule,
    MatSortModule,
    MatIconModule,
    MatListModule,
    MatSelectModule,
    MatProgressBarModule,
    MatButtonModule,
    MatChipsModule,
    MatDatepickerModule,
    MatFormFieldModule,
    MatTooltipModule,
    MatRadioModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatCheckboxModule,
    MatAutocompleteModule,
  ],
  declarations: [MiscellaneousComponent,
    HistoryComponent,
    UploadAttachementDialogComponent,
    AttachmentsComponent,
    AddressingComponent,
    ConfirmDialogComponent,
    SelectTiersTypeComponent,
    GenericDatepickerComponent,
    GenericInputComponent,
    GenericToggleComponent,
    RadioGroupCivilityComponent,
    SelectTiersCategoryComponent,
    AutocompleteCityComponent,
    SingleAttachmentComponent],
  exports: [
    HistoryComponent,
    AttachmentsComponent,
    UploadAttachementDialogComponent,
    AddressingComponent,
    ConfirmDialogComponent,
    GenericInputComponent,
    GenericDatepickerComponent,
    SelectTiersCategoryComponent,
    RadioGroupCivilityComponent,
    GenericToggleComponent,
    SelectTiersTypeComponent,
    AutocompleteCityComponent,
    SingleAttachmentComponent
  ]
})
export class MiscellaneousModule { }
