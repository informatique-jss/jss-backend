import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { AvatarComponent } from '../avatar/avatar.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';
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
  ],
  exports: [
    AvatarComponent,
    GenericInputComponent,
  ]
})
export class MiscellaneousModule { }
