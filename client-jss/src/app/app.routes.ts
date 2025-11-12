import { Routes } from '@angular/router';
import { NotFoundPageComponent } from './main/components/404/not.found.page.component';
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
        loadComponent: () => import("./main/components/post-category-header/post-category-header.component").then(m => m.PostCategoryHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/category-hub/category-hub.component").then(m => m.CategoryHubComponent),
          }
        ]
      },
      {
        path: 'post/category/:slug/:isDisplayNews',
        loadComponent: () => import('./main/components/post-category-header/post-category-header.component').then(m => m.PostCategoryHeaderComponent)
      },
      {
        path: 'post/tag/:slug',
        loadComponent: () => import('./main/components/post-tag-header/post-tag-header.component').then(m => m.PostTagHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/tag-hub/tag-hub.component").then(m => m.TagHubComponent),
          }
        ]
      },
      {
        path: 'post/tag/:slug/:isDisplayNews',
        loadComponent: () => import('./main/components/post-tag-header/post-tag-header.component').then(m => m.PostTagHeaderComponent)
      },
      {
        path: 'post/author/:slug',
        loadComponent: () => import('./main/components/post-author-header/post-author-header.component').then(m => m.PostAuthorHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/author-hub/author-hub.component").then(m => m.AuthorHubComponent),
          }
        ]
      },
      {
        path: 'post/author/:slug/:isDisplayNews',
        loadComponent: () => import('./main/components/post-author-header/post-author-header.component').then(m => m.PostAuthorHeaderComponent)
      },
      {
        path: 'post/serie/:slug',
        loadComponent: () => import('./main/components/post-serie-header/post-serie-header.component').then(m => m.PostSerieHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/serie-hub/serie-hub.component").then(m => m.SerieHubComponent),
          }
        ]
      },
      {
        path: 'post/department/:code',
        loadComponent: () => import('./main/components/post-department-header/post-department-header.component').then(m => m.PostDepartmentHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/department-hub/department-hub.component").then(m => m.DepartmentHubComponent),
          }
        ]
      },
      {
        path: 'post/tendency',
        loadComponent: () => import('./main/components/post-tendency-header/post-tendency-header.component').then(m => m.PostTendencyHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/tendency-hub/tendency-hub.component").then(m => m.TendencyHubComponent),
          }
        ]
      },
      {
        path: 'post/last',
        loadComponent: () => import('./main/components/post-last-header/post-last-header.component').then(m => m.PostLastHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/last-posts-hub/last-posts-hub.component").then(m => m.LastPostsHubComponent),
          }
        ]
      },
      {
        path: 'post/most-seen',
        loadComponent: () => import('./main/components/post-most-seen-header/post-most-seen-header.component').then(m => m.PostMostSeenHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/most-seen-hub/most-seen-hub.component").then(m => m.MostSeenHubComponent),
          }
        ]
      }, {
        path: 'post/premium',
        loadComponent: () => import('./main/components/post-premium-header/post-premium-header.component').then(m => m.PostPremiumHeaderComponent),
        children: [
          {
            path: ':page-number',
            loadComponent: () => import("./main/components/premium-hub/premium-hub.component").then(m => m.PremiumHubComponent),
          }
        ]
      },
      {
        path: 'post/:slug',
        loadComponent: () => import('./main/components/post/post.component').then(m => m.PostComponent)
      },
      {
        path: 'posts/:token/:mail',
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
      { path: '**', component: NotFoundPageComponent }
    ]
  }
];
