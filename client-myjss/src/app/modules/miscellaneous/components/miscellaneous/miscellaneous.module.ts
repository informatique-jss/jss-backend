import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { GenericTextareaComponent } from '../../generic-textarea/generic-textarea.component';
import { AvatarComponent } from '../avatar/avatar.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';
import { SingleUploadComponent } from '../single-upload/single-upload.component';
import { MiscellaneousComponent } from './miscellaneous.component';

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  declarations: [MiscellaneousComponent,
    AvatarComponent,
    GenericInputComponent,
    SingleUploadComponent,
    TrustHtmlPipe,
    GenericTextareaComponent,
  ],
  exports: [
    AvatarComponent,
    GenericInputComponent,
    SingleUploadComponent,
    TrustHtmlPipe,
    GenericTextareaComponent,
  ]
})
export class MiscellaneousModule { }
