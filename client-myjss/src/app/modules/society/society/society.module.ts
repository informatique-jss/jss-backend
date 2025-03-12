import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { AboutUsComponent } from '../about-us/about-us.component';
import { OurStoryComponent } from '../our-story/our-story.component';
import { SocietyComponent } from './society.component';

@NgModule({
  imports: [
    CommonModule,
  ],
  declarations: [SocietyComponent,
    AboutUsComponent,
    OurStoryComponent,

  ]
})
export class SocietyModule { }
