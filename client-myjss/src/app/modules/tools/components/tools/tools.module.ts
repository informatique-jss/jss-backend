import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { GeneralModule } from '../../../general/components/general/general.module';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { PostComponent } from '../post/post.component';
import { PracticalSheetsComponent } from '../practical-sheets/practical-sheets.component';
import { ToolsComponent } from './tools.component';

const routes: Routes = [
  {
    path: 'tools', component: ToolsComponent,
    children: [
      { path: 'practical-sheets', component: PracticalSheetsComponent },
    ]
  },
  { path: 'post/:slug', component: PostComponent },
];

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload' }),
    CommonModule,
    MiscellaneousModule,
    FormsModule,
    GeneralModule,
  ],
  declarations: [
    ToolsComponent,
    PostComponent,
    PracticalSheetsComponent,
  ], schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class ToolsModule { }
