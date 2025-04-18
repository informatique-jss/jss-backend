import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { CommunicationPreferenceComponent } from '../communication-preference/communication-preference.component';
import { MyjssCommentManagementComponent } from '../myjss-comment-management/myjss-comment-management.component';
import { CrmComponent } from './crm.component';


const routes: Routes = [
  { path: 'crm', component: CrmComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MiscellaneousModule,
    MatTableModule,
    MatTabsModule,
    MatIconModule,
  ],
  declarations: [
    CrmComponent,
    CommunicationPreferenceComponent,
    MyjssCommentManagementComponent
  ],
  exports: [
    CommunicationPreferenceComponent,
  ]
})
export class CrmModule { }
