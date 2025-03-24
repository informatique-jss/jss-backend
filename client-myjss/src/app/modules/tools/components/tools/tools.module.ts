import { CommonModule } from '@angular/common';
import { CUSTOM_ELEMENTS_SCHEMA, NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
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
  ],
  declarations: [
    ToolsComponent,
    PostComponent
  ], schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA]
})
export class ToolsModule { }
