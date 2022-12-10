import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from 'src/app/modules/miscellaneous/components/miscellaneous/miscellaneous.module';
import { AddJournalComponent } from '../add-journal/add-journal.component';
import { JournalListComponent } from '../journal-list/journal-list.component';
import { PaoComponent } from './pao.component';

const routes: Routes = [
  { path: 'pao', component: PaoComponent },
  { path: 'journal', component: JournalListComponent },
  { path: 'journal/add', component: AddJournalComponent },
  { path: 'journal/add/:id', component: AddJournalComponent },
];


@NgModule({
  imports: [
    CommonModule,
    MatIconModule,
    MatButtonModule,
    MatChipsModule,
    MatTooltipModule,
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    MiscellaneousModule,
  ],
  declarations: [PaoComponent,
    JournalListComponent,
    AddJournalComponent,
  ]
})
export class PaoModule { }
