import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: 'subscription', renderMode: RenderMode.Prerender },
  { path: 'kiosk', renderMode: RenderMode.Prerender },
  { path: 'newsletter', renderMode: RenderMode.Prerender },
  { path: 'contribute', renderMode: RenderMode.Prerender },
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
