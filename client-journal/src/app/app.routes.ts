import { Routes } from '@angular/router';
import { AboutComponent } from './main/components/about/about.component';
import { ArticlesCategoryComponent } from './main/components/articles-category/articles-category.component';
import { ContactUsComponent } from './main/components/contact-us/contact-us.component';
import { MainComponent } from './main/components/main/main.component';
import { PresentationComponent } from './main/components/presentation/presentation.component';

export const routes: Routes = [
  { path: '', component: MainComponent },
  { path: 'about', component: AboutComponent },
  { path: 'presentation', component: PresentationComponent },
  { path: 'contact-us', component: ContactUsComponent },
  { path: 'articles/myjss-category/:slug', component: ArticlesCategoryComponent },
];
