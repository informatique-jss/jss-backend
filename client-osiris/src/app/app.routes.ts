import { Routes } from '@angular/router';
import { DefaultComponent } from './modules/main/components/default/default.component';
import { ConstantsResolver } from './modules/main/services/constant.service';


const routesCrm = [
  { path: 'home', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
  { path: 'dashboards/:id', loadComponent: () => import('./modules/reporting/components/reporting-dashboard/reporting-dashboard.component').then(m => m.ReportingDashboardComponent) },
  {
    path: 'tiers',
    children: [
      {
        path: 'crm', children: [
          { path: 'home-kpi/:idTiers/:pageCode', loadComponent: () => import('./modules/tiers/tiers-responsables.component').then(m => m.TiersResponsablesComponent) },
          { path: 'main-kpi/:idTiers/:pageCode', loadComponent: () => import('./modules/tiers/tiers-responsables.component').then(m => m.TiersResponsablesComponent) },
          { path: 'business-kpi/:idTiers/:pageCode', loadComponent: () => import('./modules/tiers/tiers-responsables.component').then(m => m.TiersResponsablesComponent) },
          { path: 'customer-kpi/:idTiers/:pageCode', loadComponent: () => import('./modules/tiers/tiers-responsables.component').then(m => m.TiersResponsablesComponent) },

        ]
      },
      { path: 'osiris', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
    ]
  },
];

export const routes: Routes = [
  {
    path: '',
    component: DefaultComponent,
    resolve: { messages: ConstantsResolver },
    children: [
      { path: '', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
      ...routesCrm,
    ]
  }
];
