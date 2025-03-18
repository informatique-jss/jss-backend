import { enableProdMode } from "@angular/core";
import { platformBrowserDynamic } from "@angular/platform-browser-dynamic";
import { register as registerSwiperElements } from 'swiper/element/bundle';
import { AppModule } from "./app/app.module";
import { environment } from "./environments/environment";


registerSwiperElements();

platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));

if (environment.production) {
  enableProdMode();
}
