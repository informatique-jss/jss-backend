import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { GeneralModule } from '../../../general/components/general/general.module';
import { MiscellaneousModule } from '../../../miscellaneous/components/miscellaneous/miscellaneous.module';
import { PostComponent } from '../post/post.component';
import { ToolsComponent } from './tools.component';

const routes: Routes = [
  { path: 'tools', component: ToolsComponent },
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
    PostComponent
  ]
})
export class ToolsModule { }
