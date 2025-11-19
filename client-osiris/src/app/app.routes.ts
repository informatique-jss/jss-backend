import { Routes } from '@angular/router';
import { DefaultComponent } from './modules/main/components/default/default.component';
import { ConstantsResolver } from './modules/main/services/constant.service';


const routesCrm = [
  { path: 'home', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
  { path: 'dashboards/:id', loadComponent: () => import('./modules/reporting/components/reporting-dashboard/reporting-dashboard.component').then(m => m.ReportingDashboardComponent) },
  { path: 'tiers', loadComponent: () => import('./modules/tiers/components/tiers-list/tiers-list.component').then(m => m.TiersListComponent) },
  { path: 'responsables', loadComponent: () => import('./modules/tiers/components/responsable-list/responsable-list.component').then(m => m.ResponsableListComponent) },
  { path: 'tiers/view/:id', loadComponent: () => import('./modules/tiers/components/tiers/tiers.component').then(m => m.TiersComponent) },
  { path: 'responsable/view/:id', loadComponent: () => import('./modules/tiers/components/responsable/responsable.component').then(m => m.ResponsableComponent) },
  { path: 'tiers/crm/kpi/selection', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
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
