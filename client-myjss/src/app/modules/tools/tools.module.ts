import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MiscellaneousModule } from "../miscellaneous/components/miscellaneous/miscellaneous.module";
import { PracticalSheetsComponent } from './components/practical-sheets/practical-sheets.component';
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
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule
  ],
  declarations: [ToolsComponent,
    PracticalSheetsComponent
  ], schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class ToolsModule { }
