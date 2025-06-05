import { Routes } from '@angular/router';
import { DefaultComponent } from './main/components/default/default.component';
import { ConstantsResolver } from './services/constant.service';

export const routes: Routes = [
  {
    path: '',
    component: DefaultComponent,
    resolve: { messages: ConstantsResolver },
    children: [
      { path: '', redirectTo: '/home', pathMatch: 'full' },
      {
        path: 'home',
        loadComponent: () => import('./main/components/main/main.component').then(m => m.MainComponent)
      },
      {
        path: 'post/category/:slug',
        loadComponent: () => import('./main/components/post-category-header/post-category-header.component').then(m => m.PostCategoryHeaderComponent)
      },
      {
        path: 'post/category/:slug/:isDisplayNews',
        loadComponent: () => import('./main/components/post-category-header/post-category-header.component').then(m => m.PostCategoryHeaderComponent)
      },
      {
        path: 'post/tag/:slug',
        loadComponent: () => import('./main/components/post-tag-header/post-tag-header.component').then(m => m.PostTagHeaderComponent)
      },
      {
        path: 'post/tag/:slug/:isDisplayNews',
        loadComponent: () => import('./main/components/post-tag-header/post-tag-header.component').then(m => m.PostTagHeaderComponent)
      },
      {
        path: 'post/author/:slug',
        loadComponent: () => import('./main/components/post-author-header/post-author-header.component').then(m => m.PostAuthorHeaderComponent)
      },
      {
        path: 'post/author/:slug/:isDisplayNews',
        loadComponent: () => import('./main/components/post-author-header/post-author-header.component').then(m => m.PostAuthorHeaderComponent)
      },
      {
        path: 'post/serie/:slug',
        loadComponent: () => import('./main/components/post-serie-header/post-serie-header.component').then(m => m.PostSerieHeaderComponent)
      },
      {
        path: 'post/department/:id',
        loadComponent: () => import('./main/components/post-department-header/post-department-header.component').then(m => m.PostDepartmentHeaderComponent)
      },
      {
        path: 'post/tendency',
        loadComponent: () => import('./main/components/post-tendency-header/post-tendency-header.component').then(m => m.PostTendencyHeaderComponent)
      },
      {
        path: 'post/:slug',
        loadComponent: () => import('./main/components/post/post.component').then(m => m.PostComponent)
      },
      {
        path: 'post/:slug/:token/:mail',
        loadComponent: () => import('./main/components/post/post.component').then(m => m.PostComponent)
      },
      {
        path: 'announcement/search',
        loadComponent: () => import('./main/components/search-announcement/search-announcement.component').then(m => m.SearchAnnouncementComponent)
      },
      {
        path: 'announcement/:id',
        loadComponent: () => import('./main/components/announcement/announcement.component').then(m => m.AnnouncementComponent)
      },
      {
        path: 'podcasts',
        loadComponent: () => import('./main/components/podcasts/podcasts.component').then(m => m.PodcastsComponent)
      },
      {
        path: 'subscription',
        loadComponent: () => import('./main/components/subscription/subscription.component').then(m => m.SubscriptionComponent)
      },
      {
        path: 'kiosk',
        loadComponent: () => import('./main/components/kiosk/kiosk.component').then(m => m.KioskComponent)
      },
      {
        path: 'newsletter',
        loadComponent: () => import('./main/components/newsletter/newsletter.component').then(m => m.NewsletterComponent)
      },
      {
        path: 'contribute',
        loadComponent: () => import('./main/components/contribute/contribute.component').then(m => m.ContributeComponent)
      },
    ]
  }
];
