import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AboutUsComponent } from '../about-us/about-us.component';
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
    OurTeamComponent
  ], exports: [
  ]
})

export class CompanyModule { }
