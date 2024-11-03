import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { RouterModule, Routes } from '@angular/router';
import { CgvComponent } from '../cgv/cgv.component';
import { ConfidentialityPoliticComponent } from '../confidentiality-politic/confidentiality-politic.component';
import { JoinUsComponent } from '../join-us/join-us.component';
import { LegalMentionsComponent } from '../legal-mentions/legal-mentions.component';
import { PartnersComponent } from '../partners/partners.component';
import { WhoAreWeComponent } from '../who-are-we/who-are-we.component';
import { GeneralComponent } from './general.component';

const routes: Routes = [
  { path: 'who-are-we', component: WhoAreWeComponent },
  { path: 'join-us', component: JoinUsComponent },
  { path: 'partners', component: PartnersComponent },
  { path: 'legal-mentions', component: LegalMentionsComponent },
  { path: 'cgv', component: CgvComponent },
  { path: 'confidentiality', component: ConfidentialityPoliticComponent },
];


@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', scrollPositionRestoration: 'enabled' }),
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  declarations: [GeneralComponent,
    WhoAreWeComponent,
    JoinUsComponent,
    PartnersComponent,
    LegalMentionsComponent,
    CgvComponent,
    ConfidentialityPoliticComponent,
  ]
})
export class GeneralModule { }
