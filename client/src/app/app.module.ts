import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MAT_FORM_FIELD_DEFAULT_OPTIONS } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgProgressModule } from 'ngx-progressbar';
import { NgProgressHttpModule } from "ngx-progressbar/http";
import { AppComponent } from './app.component';
import { HttpErrorInterceptor } from './httpErrorInterceptor.service';
import { TiersModule } from './modules/tiers/components/tiers/tiers.module';
import { HeaderComponent } from './routing/header/header.component';
import { RoutingModule } from './routing/routing.module';
import { SidenavListComponent } from './routing/sidenav-list/sidenav-list.component';
import { MatTooltipModule } from '@angular/material/tooltip';


@NgModule({
  declarations: [
    AppComponent,
    SidenavListComponent,
    HeaderComponent
  ],
  imports: [
    // Core modules
    BrowserModule,
    RoutingModule,
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
    // App modules
    TiersModule
  ],
  exports: [],
  providers: [{
    provide: HTTP_INTERCEPTORS,
    useClass: HttpErrorInterceptor,
    multi: true
  },
  { provide: MAT_FORM_FIELD_DEFAULT_OPTIONS, useValue: { appearance: 'outline' } },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
