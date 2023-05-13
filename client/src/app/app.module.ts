import { DragDropModule } from "@angular/cdk/drag-drop";
import { registerLocaleData } from '@angular/common';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MAT_LEGACY_FORM_FIELD_DEFAULT_OPTIONS as MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/legacy-form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacyListModule as MatListModule } from '@angular/material/legacy-list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatLegacySnackBarModule as MatSnackBarModule } from '@angular/material/legacy-snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyTooltipModule as MatTooltipModule } from '@angular/material/legacy-tooltip';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgProgressModule } from 'ngx-progressbar';
import { NgProgressHttpModule } from "ngx-progressbar/http";
import { AppComponent } from './app.component';
import { HttpErrorInterceptor } from './httpErrorInterceptor.service';
import { AccountingModule } from './modules/accounting/components/accounting/accounting.module';
import { AdministrationModule } from './modules/administration/components/administration/administration.module';
import { DashboardModule } from "./modules/dashboard/components/dashboard/dashboard.module";
import { InvoicingModule } from "./modules/invoicing/components/invoicing/invoicing.module";
import { MiscellaneousModule } from './modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { MyProfilComponent } from './modules/profile/components/my-profil/my-profil.component';
import { QuotationModule } from './modules/quotation/components/quotation/quotation.module';
import { ReportingModule } from './modules/reporting/components/reporting/reporting.module';
import { TiersModule } from './modules/tiers/components/tiers/tiers.module';
import { HeaderComponent } from './routing/header/header.component';
import { RoutingModule } from './routing/routing.module';
import { SidenavListComponent } from './routing/sidenav-list/sidenav-list.component';

registerLocaleData(localeFr, 'fr');

@NgModule({
  declarations: [
    AppComponent,
    SidenavListComponent,
    HeaderComponent,
    MyProfilComponent,
  ],
  imports: [
    // Core modules
    BrowserModule,
    RoutingModule,
    DragDropModule,
    BrowserAnimationsModule,
    HttpClientModule,
    FormsModule,
    NgProgressModule.withConfig({
      color: "#0082ad",
      debounceTime: 200,
      spinner: false,
    }),
    NgProgressHttpModule.withConfig({
      silentApis: ['notifications']
    }),
    // Angular material modules
    MatIconModule,
    MatListModule,
    MatSidenavModule,
    MatSnackBarModule,
    MatTooltipModule,
    MatSortModule,
    MatTooltipModule,
    MatTableModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatBadgeModule,
    MatInputModule,
    // App modules
    TiersModule,
    QuotationModule,
    MiscellaneousModule,
    AdministrationModule,
    AccountingModule,
    InvoicingModule,
    DashboardModule,
    ReportingModule,
  ],
  exports: [
  ],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: HttpErrorInterceptor,
    multi: true
  },
  { provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { appearance: 'outline' } },
  { provide: LOCALE_ID, useValue: 'fr' }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

