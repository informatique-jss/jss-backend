import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSelectModule } from '@angular/material/select';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AddressingComponent } from '../addressing/addressing.component';
import { AttachmentsComponent } from '../attachments/attachments.component';
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
    MatFormFieldModule,
    MatTooltipModule,
    MatCheckboxModule,
    MatAutocompleteModule,
  ],
  declarations: [MiscellaneousComponent,
    HistoryComponent,
    UploadAttachementDialogComponent,
    AttachmentsComponent,
    AddressingComponent,
    SingleAttachmentComponent],
  exports: [
    HistoryComponent,
    AttachmentsComponent,
    UploadAttachementDialogComponent,
    AddressingComponent,
    SingleAttachmentComponent
  ]
})
export class MiscellaneousModule { }
