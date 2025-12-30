import { Routes } from '@angular/router';
import { DefaultComponent } from './modules/main/components/default/default.component';
import { ConstantsResolver } from './modules/main/services/constant.service';


const routesCrm = [
  { path: 'home', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
  { path: 'dashboards/:id', loadComponent: () => import('./modules/reporting/components/reporting-dashboard/reporting-dashboard.component').then(m => m.ReportingDashboardComponent) },
  { path: 'tiers', loadComponent: () => import('./modules/tiers/components/tiers-list/tiers-list.component').then(m => m.TiersListComponent) },
  { path: 'responsables', loadComponent: () => import('./modules/tiers/components/responsable-list/responsable-list.component').then(m => m.ResponsableListComponent) },
  { path: 'tiers/view/:id', loadComponent: () => import('./modules/tiers/components/tiers/tiers.component').then(m => m.TiersComponent) },
  { path: 'tiers/view/:id/responsable/:id', loadComponent: () => import('./modules/tiers/components/responsable/responsable.component').then(m => m.ResponsableComponent) },
  { path: 'tiers/crm/kpi/selection/:kpiCode', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
  { path: 'tiers/crm/kpi', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
  { path: 'quotation', loadComponent: () => import('./modules/quotation/components/quotation-list/quotation-list.component').then(m => m.QuotationListComponent) },
  { path: 'quotation/view/:id', loadComponent: () => import('./modules/quotation/components/quotation/quotation.component').then(m => m.QuotationComponent) },
  { path: 'customer-order', loadComponent: () => import('./modules/quotation/components/customer-order-list/customer-order-list.component').then(m => m.CustomerOrderListComponent) },
  { path: 'customer-order/view/:id', loadComponent: () => import('./modules/quotation/components/customer-order/customer-order.component').then(m => m.CustomerOrderComponent) },
  { path: 'provision', loadComponent: () => import('./modules/quotation/components/provision-list/provision-list.component').then(m => m.ProvisionListComponent) },
  { path: 'provision/view/:id', loadComponent: () => import('./modules/quotation/components/provision/provision.component').then(m => m.ProvisionComponent) },
  { path: 'invoicing', loadComponent: () => import('./modules/invoicing/components/invoicing-list/invoicing-list.component').then(m => m.InvoicingListComponent) },
  { path: 'invoicing/payments', loadComponent: () => import('./modules/invoicing/components/payment-list/payment-list.component').then(m => m.PaymentListComponent) },
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
