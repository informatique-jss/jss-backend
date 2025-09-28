import { Routes } from '@angular/router';
import { DefaultComponent } from './modules/main/components/default/default.component';
import { ConstantsResolver } from './modules/main/services/constant.service';


const routesCrm = [
  { path: 'home', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
  { path: 'dashboards/:id', loadComponent: () => import('./modules/reporting/components/reporting-dashboard/reporting-dashboard.component').then(m => m.ReportingDashboardComponent) },
  {
    path: 'tiers',
    children: [
      { path: ':idTiers', loadComponent: () => import('./modules/tiers/tiers-responsables.component').then(m => m.TiersResponsablesComponent) },
      // { path: '', loadComponent: () => import('./modules/tiers/components/tiers-selection/tiers-selection.component').then(m => m.TiersSelectionComponent) },
    ]
  },
  { path: 'crm', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
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
