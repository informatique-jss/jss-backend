import { Routes } from '@angular/router';
import { DefaultComponent } from './modules/main/components/default/default.component';
import { ConstantsResolver } from './modules/main/services/constant.service';
import { MyAccountComponent } from './modules/my-account/components/my-account/my-account.component';
import { CheckoutComponent } from './modules/quotation/components/checkout/checkout.component';
import { IdentificationComponent } from './modules/quotation/components/identification/identification.component';
import { QuotationComponent } from './modules/quotation/components/quotation/quotation.component';
import { ServicesSelectionComponent } from './modules/quotation/components/services-selection/services-selection.component';

const routesMyAccount = [
  {
    path: 'account', component: MyAccountComponent,
    children: [
      { path: 'overview', loadComponent: () => import('./modules/my-account/components/overview/overview.component').then(m => m.OverviewComponent) },
      { path: 'reading-folders', loadComponent: () => import('./modules/my-account/components/reading-folders/reading-folders.component').then(m => m.ReadingFoldersComponent) },
      { path: 'reading-folders/:idReadingFolder', loadComponent: () => import('./modules/my-account/components/bookmarks/bookmarks.component').then(m => m.BookmarksComponent) },
      { path: 'scope', loadComponent: () => import('./modules/my-account/components/scope/scope.component').then(m => m.ScopeComponent) },
      { path: 'closure', loadComponent: () => import('./modules/my-account/components/billing-closure/billing-closure.component').then(m => m.BillingClosureComponent) },
      { path: 'communication-preference', loadComponent: () => import('./modules/my-account/components/communication-preference/communication-preference.component').then(m => m.CommunicationPreferenceComponent) },
      { path: 'quotations', loadComponent: () => import('./modules/my-account/components/quotations/quotations.component').then(m => m.QuotationsComponent) },
      { path: 'orders', loadComponent: () => import('./modules/my-account/components/orders/orders.component').then(m => m.OrdersComponent) },
      { path: 'orders/details/:id', loadComponent: () => import('./modules/my-account/components/order-details/order-details.component').then(m => m.OrderDetailsComponent) },
      { path: 'order/address/edit/:idOrder', loadComponent: () => import('./modules/my-account/components/edit-address/edit-address.component').then(m => m.EditAddressComponent) },
      { path: 'order/pay/:idOrder', loadComponent: () => import('./modules/my-account/components/pay-order/pay-order.component').then(m => m.PayOrderComponent) },
      { path: 'quotation/pay/:idQuotation', loadComponent: () => import('./modules/my-account/components/pay-quotation/pay-quotation.component').then(m => m.PayQuotationComponent) },
      { path: 'quotations/details/:id', loadComponent: () => import('./modules/my-account/components/quotation-details/quotation-details.component').then(m => m.QuotationDetailsComponent) },
      { path: 'quotation/address/edit/:idQuotation', loadComponent: () => import('./modules/my-account/components/edit-address/edit-address.component').then(m => m.EditAddressComponent) },
      { path: 'affaires', loadComponent: () => import('./modules/my-account/components/affaires/affaires.component').then(m => m.AffairesComponent) },
      { path: 'affaires/:idAffaire', loadComponent: () => import('./modules/my-account/components/affaires/affaires.component').then(m => m.AffairesComponent) },
      { path: 'affaire/edit/order/:id/:idOrder', loadComponent: () => import('./modules/my-account/components/edit-affaire/edit-affaire.component').then(m => m.EditAffaireComponent) },
      { path: 'affaire/edit/quotation/:id/:idQuotation', loadComponent: () => import('./modules/my-account/components/edit-affaire/edit-affaire.component').then(m => m.EditAffaireComponent) },
      { path: 'settings', loadComponent: () => import('./modules/my-account/components/user-settings/user-settings.component').then(m => m.UserSettingsComponent) },
      { path: 'associated-settings', loadComponent: () => import('./modules/my-account/components/user-settings/user-settings.component').then(m => m.UserSettingsComponent) },
      { path: 'settings/:idResponsable', loadComponent: () => import('./modules/my-account/components/user-settings/user-settings.component').then(m => m.UserSettingsComponent) },
      { path: 'settings/address/edit/:idResponsable', loadComponent: () => import('./modules/my-account/components/edit-address/edit-address.component').then(m => m.EditAddressComponent) },
      { path: 'subscription', loadComponent: () => import('./modules/my-account/components/subscriptions/subscriptions.component').then(m => m.SubscriptionsComponent) },
      { path: 'appointment', loadComponent: () => import('./modules/my-account/components/appointment/appointment.component').then(m => m.AppointmentComponent) },
    ]
  },
  { path: 'account/signin/:from', loadComponent: () => import('./modules/my-account/components/sign-in/sign-in.component').then(m => m.SignInComponent) },
  { path: 'account/signin', loadComponent: () => import('./modules/my-account/components/sign-in/sign-in.component').then(m => m.SignInComponent) },
  { path: 'account/signout', loadComponent: () => import('./modules/my-account/components/sign-out/sign-out.component').then(m => m.SignOutComponent) },
  { path: 'profile/login', loadComponent: () => import('./modules/my-account/components/overview/overview.component').then(m => m.OverviewComponent) },
  { path: 'email-unsubscribe/:mail/:validationToken', loadComponent: () => import('./modules/my-account/components/email-communication-preference/email-communication-preference.component').then(m => m.EmailCommunicationPreferenceComponent) },
];

const routesMyService = [
  {
    path: 'services',
    loadComponent: () => import('./modules/my-services/components/myjss-services/myjss-services.component').then(m => m.MyJssServicesComponent),
    children: [
      { path: 'announcement', loadComponent: () => import('./modules/my-services/components/announcement/announcement.component').then(m => m.AnnouncementComponent) },
      { path: 'formality', loadComponent: () => import('./modules/my-services/components/formality/formality.component').then(m => m.FormalityComponent) },
      { path: 'apostille', loadComponent: () => import('./modules/my-services/components/apostille/apostille.component').then(m => m.ApostilleComponent) },
      { path: 'domiciliation', loadComponent: () => import('./modules/my-services/components/domiciliation/domiciliation.component').then(m => m.DomiciliationComponent) },
      { path: 'document', loadComponent: () => import('./modules/my-services/components/document/document.component').then(m => m.DocumentComponent) },
    ]
  },
];

const routesQuotation = [
  {
    path: 'quotation', component: QuotationComponent,
    children: [
      { path: 'identification', component: IdentificationComponent },
      { path: 'services-selection', component: ServicesSelectionComponent },
      {
        path: 'required-information',
        loadComponent: () => import('./modules/quotation/components/required-information/required-information.component').then(m => m.RequiredInformationComponent),
      },
      { path: 'checkout', component: CheckoutComponent },
      { path: 'new/:idFamilyGroup/:idQuotationType', component: IdentificationComponent },
    ]
  }, {
    path: 'quotation/subscription/:subscription-type/:is-price-reduction/:id-article',
    loadComponent: () => import('./modules/quotation/components/quotation/quotation.component').then(m => m.QuotationComponent),
  }, {
    path: 'quotation/resume/quotation/:idQuotation', component: QuotationComponent,
  }, {
    path: 'quotation/resume/order/:idOrder', component: QuotationComponent,
  },
];

const routesCompany = [
  {
    path: 'company',
    loadComponent: () => import('./modules/company/components/company/company.component').then(m => m.CompanyComponent),
    children: [
      { path: 'about-us', loadComponent: () => import('./modules/company/components/about-us/about-us.component').then(m => m.AboutUsComponent) },
      { path: 'our-story', loadComponent: () => import('./modules/company/components/our-story/our-story.component').then(m => m.OurStoryComponent) },
      { path: 'our-team', loadComponent: () => import('./modules/company/components/our-team/our-team.component').then(m => m.OurTeamComponent) },
      { path: 'join-us', loadComponent: () => import('./modules/company/components/join-us/join-us.component').then(m => m.JoinUsComponent) },
    ]
  },
];

const routesGeneral = [
  { path: 'home', loadComponent: () => import('./modules/general/components/homepage/homepage.component').then(m => m.HomepageComponent) },
  { path: 'demo', loadComponent: () => import('./modules/general/components/demo/demo.component').then(m => m.DemoComponent) },
  { path: 'prices', loadComponent: () => import('./modules/general/components/prices/prices.component').then(m => m.PricesComponent) },
  { path: 'contact', loadComponent: () => import('./modules/general/components/contact/contact.component').then(m => m.ContactComponent) },
  { path: 'newsletter', loadComponent: () => import('./modules/general/components/newsletter-subscription/newsletter-subscription.component').then(m => m.NewsletterSubscriptionComponent) },
  { path: 'privacy-policy', loadComponent: () => import('./modules/general/components/privacy-policy/privacy-policy.component').then(m => m.PrivacyPolicyComponent) },
  { path: 'disclaimer', loadComponent: () => import('./modules/general/components/privacy-policy/privacy-policy.component').then(m => m.PrivacyPolicyComponent) },
  { path: 'terms', loadComponent: () => import('./modules/general/components/privacy-policy/privacy-policy.component').then(m => m.PrivacyPolicyComponent) },
];

const routesTools = [
  {
    path: 'tools',
    loadComponent: () => import('./modules/tools/components/tools/tools.component').then(m => m.ToolsComponent),
    children: [
      { path: 'practical-sheets/:slug', loadComponent: () => import('./modules/tools/components/practical-sheets/practical-sheets.component').then(m => m.PracticalSheetsComponent) },
      { path: 'practical-sheets', loadComponent: () => import('./modules/tools/components/practical-sheets/practical-sheets.component').then(m => m.PracticalSheetsComponent) },
      { path: 'mandatory-documents', loadComponent: () => import('./modules/tools/components/mandatory-documents/mandatory-documents.component').then(m => m.MandatoryDocumentsComponent) },
      { path: 'webinars', loadComponent: () => import('./modules/tools/components/webinars/webinars.component').then(m => m.WebinarsComponent) },
      { path: 'exclusives', loadComponent: () => import('./modules/tools/components/exclusives/exclusives.component').then(m => m.ExclusivesComponent) },
    ]
  },
  { path: 'post/:slug', loadComponent: () => import('./modules/tools/components/post/post.component').then(m => m.PostComponent) },
];

export const routes: Routes = [
  {
    path: '',
    component: DefaultComponent,
    resolve: { messages: ConstantsResolver },
    children: [
      { path: '', loadComponent: () => import('./modules/general/components/homepage/homepage.component').then(m => m.HomepageComponent) },
      ...routesGeneral,
      ...routesMyAccount,
      ...routesMyService,
      ...routesQuotation,
      ...routesCompany,
      ...routesTools,
    ]
  }
];
