import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';
import { MatLegacyDialogModule as MatDialogModule } from '@angular/material/legacy-dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacyTabsModule as MatTabsModule } from '@angular/material/legacy-tabs';
import { RouterModule, Routes } from '@angular/router';
import { MyProfilComponent } from '../modules/profile/components/my-profil/my-profil.component';
import { LoginDialogComponent } from './login-dialog/login-dialog.component';
import { SearchComponent } from './search/search.component';

const routes: Routes = [
  { path: '', redirectTo: '/provision', pathMatch: 'full' },
  { path: 'my-profil', component: MyProfilComponent },
];

@NgModule({
  declarations: [SearchComponent,
    LoginDialogComponent,
  ],
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    MatInputModule,
    MatTabsModule,
    MatDividerModule,
    MatIconModule,
    MatDialogModule,
    MatCardModule,
    MatButtonModule
  ],
  exports: [
    RouterModule,
  ]
})
export class RoutingModule { }
