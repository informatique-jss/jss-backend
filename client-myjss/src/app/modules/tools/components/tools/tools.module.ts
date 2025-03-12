import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { PracticalSheetsComponent } from '../practical-sheets/practical-sheets.component';
import { ToolsComponent } from './tools.component';


const routes: Routes = [
  {
    path: 'tools', component: ToolsComponent,
    children: [
      { path: 'practical-sheets', component: PracticalSheetsComponent },
    ]
  },
];

@NgModule({
  imports: [
    RouterModule.forChild(routes),
    CommonModule,
    MiscellaneousModule,
  ],
  declarations: [
    ToolsComponent,
    PracticalSheetsComponent,
  ]
})
export class ToolsModule { }
