import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatTabsModule } from '@angular/material/tabs';
import { RouterModule, Routes } from '@angular/router';
import { MyProfilComponent } from '../modules/profile/components/my-profil/my-profil.component';
import { LoginDialogComponent } from './login-dialog/login-dialog.component';
import { SearchComponent } from './search/search.component';

const routes: Routes = [
  { path: '', redirectTo: '/affaire', pathMatch: 'full' },
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
