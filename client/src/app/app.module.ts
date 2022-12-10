import { DragDropModule } from "@angular/cdk/drag-drop";
import { registerLocaleData } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatBadgeModule } from '@angular/material/badge';
import { MatButtonModule } from '@angular/material/button';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatSortModule } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgProgressModule } from 'ngx-progressbar';
import { NgProgressHttpModule } from "ngx-progressbar/http";
import { AppComponent } from './app.component';
import { HttpErrorInterceptor } from './httpErrorInterceptor.service';
import { AccountingModule } from './modules/accounting/components/accounting/accounting.module';
import { AdministrationModule } from './modules/administration/components/administration/administration.module';
import { DashboardModule } from './modules/dashboard/dashboard.module';
import { InvoicingModule } from "./modules/invoicing/components/invoicing/invoicing.module";
import { MiscellaneousModule } from './modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { PaoModule } from "./modules/pao/components/pao/pao.module";
import { MyProfilComponent } from './modules/profile/components/my-profil/my-profil.component';
import { QuotationModule } from './modules/quotation/components/quotation/quotation.module';
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
    NgProgressHttpModule,
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
    PaoModule,
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

