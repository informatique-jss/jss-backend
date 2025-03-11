import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { CommunicationPreferenceComponent } from '../communication-preference/communication-preference.component';
import { CrmComponent } from './crm.component';

@NgModule({
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MiscellaneousModule
  ],
  declarations: [
    CrmComponent,
    CommunicationPreferenceComponent,
  ],
  exports: [
    CommunicationPreferenceComponent,
  ]
})
export class CrmModule { }
