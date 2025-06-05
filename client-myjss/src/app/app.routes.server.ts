import { RenderMode, ServerRoute } from '@angular/ssr';

export const serverRoutes: ServerRoute[] = [
  { path: 'quotation/required-information', renderMode: RenderMode.Client },
  { path: 'account/signout', renderMode: RenderMode.Client },
  { path: 'profile/login', renderMode: RenderMode.Client },
  { path: 'quotation/identification', renderMode: RenderMode.Client },
  { path: 'quotation/services-selection', renderMode: RenderMode.Client },
  { path: 'quotation/checkout', renderMode: RenderMode.Client },
  { path: 'account', renderMode: RenderMode.Client },
  { path: 'account/communication-preference', renderMode: RenderMode.Client },
  { path: 'account/bookmarks', renderMode: RenderMode.Client },
  { path: 'account/signin', renderMode: RenderMode.Prerender },
  { path: 'account/signout', renderMode: RenderMode.Prerender },

  { path: 'services', renderMode: RenderMode.Prerender },

  { path: 'company', renderMode: RenderMode.Prerender },
  { path: 'company/about-us', renderMode: RenderMode.Prerender },
  { path: 'company/our-story', renderMode: RenderMode.Prerender },
  { path: 'company/our-team', renderMode: RenderMode.Prerender },
  { path: 'company/join-us', renderMode: RenderMode.Prerender },

  { path: 'demo', renderMode: RenderMode.Prerender },
  { path: 'prices', renderMode: RenderMode.Prerender },
  { path: 'contact', renderMode: RenderMode.Prerender },
  { path: 'newsletter', renderMode: RenderMode.Prerender },
  { path: 'privacy-policy', renderMode: RenderMode.Prerender },
  { path: 'disclaimer', renderMode: RenderMode.Prerender },
  { path: 'terms', renderMode: RenderMode.Prerender },

  { path: 'tools', renderMode: RenderMode.Prerender },
  { path: 'tools/mandatory-documents', renderMode: RenderMode.Prerender },
  { path: 'tools/webinars', renderMode: RenderMode.Prerender },
  {
    path: '**',
    renderMode: RenderMode.Server
  }
];
