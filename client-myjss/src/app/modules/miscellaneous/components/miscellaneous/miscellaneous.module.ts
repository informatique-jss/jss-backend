import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { TrustHtmlPipe } from '../../../../libs/TrustHtmlPipe';
import { AccessPlanComponent } from '../access-plan/access-plan.component';
import { AvatarComponent } from '../avatar/avatar.component';
import { GenericInputComponent } from '../forms/generic-input/generic-input.component';
import { GenericTextareaComponent } from '../forms/generic-textarea/generic-textarea.component';
import { SingleUploadComponent } from '../forms/single-upload/single-upload.component';
import { MiscellaneousComponent } from './miscellaneous.component';

const routes: Routes = [
  {
    path: 'access-plan', component: AccessPlanComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
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
    AccessPlanComponent
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
