import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { TimeFormatPipe } from '../../../libs/TimeFormatPipe';
import { TrustHtmlPipe } from '../../../libs/TrustHtmlPipe';
import { AnnouncementComponent } from '../announcement/announcement.component';
import { AuthorHubComponent } from '../author-hub/author-hub.component';
import { CategoryHubComponent } from '../category-hub/category-hub.component';
import { ContributeComponent } from '../contribute/contribute.component';
import { DepartmentHubComponent } from '../department-hub/department-hub.component';
import { GenericInputComponent } from '../generic-input/generic-input.component';
import { HeaderComponent } from '../header/header.component';
import { IdfHubComponent } from '../idf-hub/idf-hub.component';
import { KioskComponent } from '../kiosk/kiosk.component';
import { NewsletterComponent } from '../newsletter/newsletter.component';
import { PodcastsComponent } from '../podcasts/podcasts.component';
import { PostAuthorHeaderComponent } from '../post-author-header/post-author-header.component';
import { PostCategoryHeaderComponent } from '../post-category-header/post-category-header.component';
import { PostDepartmentHeaderComponent } from '../post-department-header/post-department-header.component';
import { PostIdfHeaderComponent } from '../post-idf-header/post-idf-header.component';
import { PostSerieHeaderComponent } from '../post-serie-header/post-serie-header.component';
import { PostTagHeaderComponent } from '../post-tag-header/post-tag-header.component';
import { PostComponent } from '../post/post.component';
import { SearchAnnouncementComponent } from '../search-announcement/search-announcement.component';
import { SerieHubComponent } from '../serie-hub/serie-hub.component';
import { SubscriptionComponent } from '../subscription/subscription.component';
import { TagHubComponent } from '../tag-hub/tag-hub.component';
import { MainComponent } from './main.component';

const routes: Routes = [
  { path: 'post/category/:slug', component: PostCategoryHeaderComponent },
  { path: 'post/tag/:slug', component: PostTagHeaderComponent },
  { path: 'post/author/:slug', component: PostAuthorHeaderComponent },
  { path: 'post/serie/:slug', component: PostSerieHeaderComponent },
  { path: 'post/department/:id', component: PostDepartmentHeaderComponent },
  { path: 'post/department/idf/all', component: PostIdfHeaderComponent },
  { path: 'home', component: MainComponent },
  { path: 'post/:slug', component: PostComponent },
  { path: 'announcement/search', component: SearchAnnouncementComponent },
  { path: 'announcement/:id', component: AnnouncementComponent },
  { path: 'podcasts', component: PodcastsComponent },
  { path: 'subscription', component: SubscriptionComponent },
  { path: 'kiosk', component: KioskComponent },
  { path: 'contribute', component: ContributeComponent },
];

@NgModule({
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forRoot(routes, { onSameUrlNavigation: 'reload', scrollPositionRestoration: 'enabled' }),
    NewsletterComponent
  ],
  declarations: [
    MainComponent,
    HeaderComponent,
    TrustHtmlPipe,
    PostComponent,
    SearchAnnouncementComponent,
    TimeFormatPipe,
    AnnouncementComponent,
    CategoryHubComponent,
    TagHubComponent,
    AuthorHubComponent,
    SerieHubComponent,
    DepartmentHubComponent,
    IdfHubComponent,
    PostCategoryHeaderComponent,
    PostTagHeaderComponent,
    PostAuthorHeaderComponent,
    PostSerieHeaderComponent,
    PostDepartmentHeaderComponent,
    PostIdfHeaderComponent,
    GenericInputComponent,
    PodcastsComponent,
    SubscriptionComponent,
    KioskComponent,
    ContributeComponent
  ],
  exports: [
    HeaderComponent,
    NewsletterComponent
  ]
})
export class MainModule { }
