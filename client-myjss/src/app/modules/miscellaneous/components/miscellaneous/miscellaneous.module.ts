import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { Routes } from '@angular/router';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { AvatarComponent } from '../avatar/avatar.component';
import { GenericInputComponent } from '../forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../forms/generic-textarea/generic-textarea.component';
import { GenericToggleComponent } from '../forms/generic-toggle/generic-toggle.component';
import { SingleUploadComponent } from '../forms/single-upload/single-upload.component';
import { MiscellaneousComponent } from './miscellaneous.component';

const routes: Routes = [
  {
  }
];

@NgModule({
  imports: [
    // RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
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
    GenericToggleComponent,
  ],
  exports: [
    AvatarComponent,
    GenericInputComponent,
    SingleUploadComponent,
    TrustHtmlPipe,
    GenericTextareaComponent,
    GenericToggleComponent,
  ]
})
export class MiscellaneousModule { }
