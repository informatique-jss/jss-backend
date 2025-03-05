import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { MyAccountModule } from "../../my-account/components/my-account/my-account.module";
import { AnnouncementComponent } from '../announcement/announcement.component';
import { ExplainationVideoComponent } from '../explaination-video/explaination-video.component';
import { JssServicesComponent } from './jss-services.component';

const routes: Routes = [
  { path: 'services', component: JssServicesComponent },
  { path: 'services/announcement', component: AnnouncementComponent },

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule,
    MyAccountModule
  ],
  declarations: [JssServicesComponent,
    AnnouncementComponent,
    ExplainationVideoComponent
  ]
})
export class MyServicesModule { }
