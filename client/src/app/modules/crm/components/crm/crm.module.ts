import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { CandidacyComponent } from '../candidacy/candidacy.component';
import { CommunicationPreferenceComponent } from '../communication-preference/communication-preference.component';
import { MyjssCommentManagementComponent } from '../myjss-comment-management/myjss-comment-management.component';
import { NewVoucherDialogComponent } from '../new-voucher-dialog/new-voucher-dialog.component';
import { VoucherComponent } from '../voucher/voucher.component';
import { WebinarParticipantComponent } from '../webinar-participant/webinar-participant.component';
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
    MatButtonModule,
    MatTableModule,
    MatTabsModule,
    MatIconModule,
    MatButtonModule
  ],
  declarations: [
    CrmComponent,
    CommunicationPreferenceComponent,
    MyjssCommentManagementComponent,
    WebinarParticipantComponent,
    CandidacyComponent,
    VoucherComponent,
    NewVoucherDialogComponent
  ],
  exports: [
    CommunicationPreferenceComponent,
  ]
})
export class CrmModule { }
