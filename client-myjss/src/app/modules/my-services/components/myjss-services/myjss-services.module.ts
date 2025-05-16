import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { MyAccountModule } from "../../../my-account/components/my-account/my-account.module";
import { AnnouncementComponent } from '../announcement/announcement.component';
import { ApostilleComponent } from '../apostille/apostille.component';
import { DescriptionMyAccountComponent } from '../description-my-account/description-my-account.component';
import { DocumentComponent } from '../document/document.component';
import { DomiciliationComponent } from '../domiciliation/domiciliation.component';
import { ExplainationVideoComponent } from '../explaination-video/explaination-video.component';
import { FormalityComponent } from '../formality/formality.component';
import { MyJssServicesComponent } from './myjss-services.component';

const routes: Routes = [
  {
    path: 'services', component: MyJssServicesComponent,
    children: [
      { path: 'announcement', component: AnnouncementComponent },
      { path: 'formality', component: FormalityComponent },
      { path: 'apostille', component: ApostilleComponent },
      { path: 'domiciliation', component: DomiciliationComponent },
      { path: 'document', component: DocumentComponent },
    ]
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule,
    MyAccountModule
  ],
  declarations: [MyJssServicesComponent,
    AnnouncementComponent,
    ExplainationVideoComponent,
    FormalityComponent,
    DomiciliationComponent,
    ApostilleComponent,
    DocumentComponent,
    DescriptionMyAccountComponent
  ]
})
export class MyJssServicesModule { }
