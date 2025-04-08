import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { HomepageComponent } from '../homepage/homepage.component';
import { NewsletterComponent } from '../newsletter/newsletter.component';
import { GeneralComponent } from './general.component';

const routes: Routes = [
  { path: 'home', component: HomepageComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', scrollPositionRestoration: 'enabled' }),
    CommonModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    MiscellaneousModule
  ],
  declarations: [GeneralComponent,
    HomepageComponent,
    NewsletterComponent,
  ], exports: [
    NewsletterComponent
  ]
})
export class GeneralModule { }
