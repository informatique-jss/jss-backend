import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MiscellaneousComponent } from './miscellaneous.component';
import { HistoryComponent } from '../history/history.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatTableModule } from '@angular/material/table';
import { MatSortModule } from '@angular/material/sort';
import { AttachmentsComponent } from '../attachments/attachments.component';
import { MatIconModule } from '@angular/material/icon';
import { UploadAttachementDialogComponent } from '../upload-attachement-dialog/upload-attachement-dialog.component';
import { MatListModule } from '@angular/material/list';
import { MatSelectModule } from '@angular/material/select';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatButtonModule } from '@angular/material/button';
import { AddressingComponent } from '../addressing/addressing.component';
import { MatChipsModule } from '@angular/material/chips';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatAutocompleteModule } from '@angular/material/autocomplete';

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
    MatFormFieldModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatAutocompleteModule
  ],
  declarations: [MiscellaneousComponent,
    HistoryComponent,
    UploadAttachementDialogComponent,
    AttachmentsComponent,
    AddressingComponent],
  exports: [
    HistoryComponent,
    AttachmentsComponent,
    UploadAttachementDialogComponent,
    AddressingComponent
  ]
})
export class MiscellaneousModule { }
