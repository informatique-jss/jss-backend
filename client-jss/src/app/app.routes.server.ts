import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: 'home', renderMode: RenderMode.Server },
  { path: 'post/category/:slug', renderMode: RenderMode.Server },
  { path: 'post/tag/:slug', renderMode: RenderMode.Server },
  { path: 'post/author/:slug', renderMode: RenderMode.Server },
  { path: 'post/serie/:slug', renderMode: RenderMode.Server },
  { path: 'post/department/:id', renderMode: RenderMode.Server },
  { path: 'post/:slug', renderMode: RenderMode.Server },
  { path: 'announcement/search', renderMode: RenderMode.Server },
  { path: 'announcement/:id', renderMode: RenderMode.Server },
  { path: 'podcasts', renderMode: RenderMode.Server },
  { path: 'subscription', renderMode: RenderMode.Prerender },
  { path: 'kiosk', renderMode: RenderMode.Prerender },
  { path: 'newsletter', renderMode: RenderMode.Prerender },
  { path: 'contribute', renderMode: RenderMode.Prerender },
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
