import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from "../../../miscellaneous/components/miscellaneous/miscellaneous.module";
import { AboutUsComponent } from '../about-us/about-us.component';
import { JoinUsComponent } from '../join-us/join-us.component';
import { OurStorySwiperComponent } from '../our-story-swiper/our-story-swiper.component';
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
    MiscellaneousModule,
    OurStorySwiperComponent
  ],
  declarations: [CompanyComponent,
    AboutUsComponent,
    OurStoryComponent,
    OurTeamComponent,
    JoinUsComponent
  ], exports: [
    OurStoryComponent,
    OurStorySwiperComponent
  ]
})

export class CompanyModule { }
