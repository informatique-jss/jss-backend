import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { AnnouncementComponent } from '../announcement/announcement.component';
import { JssServicesComponent } from './jss-services.component';

const routes: Routes = [
  { path: 'services', component: JssServicesComponent },
  { path: 'services/announcement', component: AnnouncementComponent },

];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule
  ],
  declarations: [JssServicesComponent,
    AnnouncementComponent
  ]
})
export class MyServicesModule { }
