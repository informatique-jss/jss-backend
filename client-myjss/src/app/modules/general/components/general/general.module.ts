import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { NotFoundPageComponent } from '../404/not.found.page.component';
import { ContactComponent } from '../contact/contact.component';
import { DemoComponent } from '../demo/demo.component';
import { HomepageComponent } from '../homepage/homepage.component';
import { NewsletterSubscriptionComponent } from '../newsletter-subscription/newsletter-subscription.component';
import { NewsletterComponent } from '../newsletter/newsletter.component';
import { PricesComponent } from '../prices/prices.component';
import { PrivacyPolicyComponent } from '../privacy-policy/privacy-policy.component';
import { GeneralComponent } from './general.component';

const routes: Routes = [
  { path: 'home', component: HomepageComponent },
  { path: 'demo', component: DemoComponent },
  { path: 'prices', component: PricesComponent },
  { path: 'contact', component: ContactComponent },
  { path: 'newsletter', component: NewsletterSubscriptionComponent },
  { path: 'privacy-policy', component: PrivacyPolicyComponent },
  { path: 'disclaimer', component: PrivacyPolicyComponent },
  { path: 'terms', component: PrivacyPolicyComponent },
  { path: '**', component: NotFoundPageComponent },
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
    DemoComponent,
    PricesComponent,
    ContactComponent,
    NewsletterSubscriptionComponent,
    PrivacyPolicyComponent,
    NotFoundPageComponent,
  ], exports: [
    NewsletterComponent
  ]
})
export class GeneralModule { }
