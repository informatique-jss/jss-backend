import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { TrustHtmlPipe } from '../../../libs/TrustHtmlPipe';
import { ArticlesCategoryComponent } from '../articles-category/articles-category.component';
import { BodyArticlesComponent } from '../body-articles/body-articles.component';
import { FooterComponent } from '../footer/footer.component';
import { HeaderComponent } from '../header/header.component';
import { NewArticlesComponent } from '../new-articles/new-articles.component';
import { OffCanvasComponent } from '../off-canvas/off-canvas.component';
import { PresentationComponent } from '../presentation/presentation.component';
import { TrendComponent } from '../trend/trend.component';
import { MainComponent } from './main.component';

@NgModule({
  imports: [
    CommonModule
  ],
  declarations: [
    MainComponent,
    OffCanvasComponent,
    HeaderComponent,
    PresentationComponent,
    TrendComponent,
    NewArticlesComponent,
    TrustHtmlPipe,
    BodyArticlesComponent,
    ArticlesCategoryComponent,
    FooterComponent,
  ],
  exports: [
    OffCanvasComponent,
    HeaderComponent,
  ]
})
export class MainModule { }
