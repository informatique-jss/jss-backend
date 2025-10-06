import { Routes } from '@angular/router';
import { DefaultComponent } from './modules/main/components/default/default.component';
import { ConstantsResolver } from './modules/main/services/constant.service';


const routesCrm = [
  { path: 'home', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
  { path: 'dashboards/:id', loadComponent: () => import('./modules/reporting/components/reporting-dashboard/reporting-dashboard.component').then(m => m.ReportingDashboardComponent) },
  {
    path: 'tiers/home-kpi',
    children: [
      { path: ':idTiers', loadComponent: () => import('./modules/tiers/tiers-responsables.component').then(m => m.TiersResponsablesComponent) },
      // { path: '', loadComponent: () => import('./modules/tiers/components/tiers-selection/tiers-selection.component').then(m => m.TiersSelectionComponent) },
    ]
  },
  { path: 'tiers/main-kpi/:idTiers', loadComponent: () => import('./modules/tiers/components/responsables-main-kpi/responsables-main-kpi.component').then(m => m.ResponsablesMainKpiComponent) },
  { path: 'tiers/business-kpi/:idTiers', loadComponent: () => import('./modules/tiers/components/responsables-business-kpi/responsables-business-kpi.component').then(m => m.ResponsablesBusinessKpiComponent) },
  { path: 'tiers/customer-kpi/:idTiers', loadComponent: () => import('./modules/tiers/components/responsables-customer-kpi/responsables-customer-kpi.component').then(m => m.ResponsablesCustomerKpiComponent) },
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
