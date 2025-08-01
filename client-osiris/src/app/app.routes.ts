import { Routes } from '@angular/router';
import { DefaultComponent } from './modules/main/components/default/default.component';
import { ConstantsResolver } from './modules/main/services/constant.service';


const routesCrm = [
  { path: 'home', loadComponent: () => import('./modules/crm/components/crm/crm.component').then(m => m.CrmComponent) },
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
