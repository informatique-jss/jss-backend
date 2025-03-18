import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AboutUsComponent } from '../about-us/about-us.component';
import { JoinUsComponent } from '../join-us/join-us.component';
import { OurStoryComponent } from '../our-story/our-story.component';
import { OurTeamComponent } from '../our-team/our-team.component';
import { CompanyComponent } from './company.component';

const routes: Routes = [
  {
    path: 'company', component: CompanyComponent,
    children: [
      { path: 'about-us', component: AboutUsComponent },
      { path: 'our-story', component: OurStoryComponent },
      { path: 'our-team', component: OurTeamComponent },
      { path: 'join-us', component: JoinUsComponent },
    ]
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
  ],
  declarations: [CompanyComponent,
    AboutUsComponent,
    OurStoryComponent,
    OurTeamComponent,
    JoinUsComponent
  ], exports: [
    OurStoryComponent
  ], schemas: [CUSTOM_ELEMENTS_SCHEMA] // To let Angular accept Swiper components

})

export class CompanyModule { }
