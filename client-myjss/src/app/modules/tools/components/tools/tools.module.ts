import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
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
  ],
  declarations: [
    ToolsComponent,
    PostComponent
  ]
})
export class ToolsModule { }
