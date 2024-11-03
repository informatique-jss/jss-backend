import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { TimeFormatPipe } from '../../../libs/TimeFormatPipe';
import { TrustHtmlPipe } from '../../../libs/TrustHtmlPipe';
import { ArticlesCategoryComponent } from '../articles-category/articles-category.component';
import { AuthorListComponent } from '../author-list/author-list.component';
import { BodyArticlesComponent } from '../body-articles/body-articles.component';
import { ContactUsComponent } from '../contact-us/contact-us.component';
import { DepartmentListComponent } from '../department-list/department-list.component';
import { HeaderComponent } from '../header/header.component';
import { InterviewListComponent } from '../interview-list/interview-list.component';
import { NewArticlesComponent } from '../new-articles/new-articles.component';
import { PodcastListComponent } from '../podcast-list/podcast-list.component';
import { PodcastPostComponent } from '../podcast-post/podcast-post.component';
import { PostComponent } from '../post/post.component';
import { SearchAnnouncementComponent } from '../search-announcement/search-announcement.component';
import { SerieListComponent } from '../serie-list/serie-list.component';
import { SerieComponent } from '../serie/serie.component';
import { TagListComponent } from '../tag-list/tag-list.component';
import { TrendComponent } from '../trend/trend.component';
import { MainComponent } from './main.component';

const routes: Routes = [
  { path: 'post/:slug', component: PostComponent },
  { path: 'author/:slug', component: AuthorListComponent },
  { path: 'category/:slug', component: ArticlesCategoryComponent },
  { path: 'podcasts', component: PodcastListComponent },
  { path: 'podcast/:slug', component: PodcastPostComponent },
  { path: 'tag/:slug', component: TagListComponent },
  { path: 'interviews', component: InterviewListComponent },
  { path: 'series', component: SerieListComponent },
  { path: 'serie/:slug', component: SerieComponent },
  { path: 'contact', component: ContactUsComponent },
  { path: 'department/:code', component: DepartmentListComponent },
  { path: 'announcement/search', component: SearchAnnouncementComponent },
];

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', scrollPositionRestoration: 'enabled' }),
  ],
  declarations: [
    MainComponent,
    HeaderComponent,
    ContactUsComponent,
    TrendComponent,
    NewArticlesComponent,
    TrustHtmlPipe,
    BodyArticlesComponent,
    ArticlesCategoryComponent,
    PostComponent,
    DepartmentListComponent,
    InterviewListComponent,
    SerieListComponent,
    PodcastListComponent,
    AuthorListComponent,
    PodcastPostComponent,
    SerieComponent,
    SearchAnnouncementComponent,
    TagListComponent,
    TimeFormatPipe
  ],
  exports: [
    HeaderComponent,
  ]
})
export class MainModule { }
