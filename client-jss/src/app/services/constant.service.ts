import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { Constant } from '../libs/Constant';
import { AppRestService } from './appRest.service';
import { PlatformService } from './platform.service';

@Injectable({
  providedIn: 'root',
})
export class ConstantService extends AppRestService<Constant> {

  constructor(http: HttpClient, private platformService: PlatformService) {
    super(http, "quotation");
  }

  constant: Constant = {} as Constant;

  getConstants() {
    return this.get(new HttpParams(), "constants");
  }

  initConstant() {
    if (this.platformService.isBrowser() && this.platformService.getNativeLocalStorage()!.getItem('constants') != null) {
      let a = this.platformService.getNativeLocalStorage()!.getItem('constants');
      this.constant = JSON.parse(a!) as Constant;
    }

    this.getConstants().subscribe(response => {
      this.constant = response;
      if (this.platformService.isBrowser())
        this.platformService.getNativeLocalStorage()!.setItem('constants', JSON.stringify(this.constant));
    });
  }

  initConstantFromResolver(): Observable<Constant> {
    if (this.constant && this.constant.id) {
      return of(this.constant);
    }

    if (this.platformService.isBrowser()) {
      const stored = this.platformService.getNativeLocalStorage()?.getItem('constants');
      if (stored) {
        this.constant = JSON.parse(stored) as Constant;
        return of(this.constant);
      }
    }

    return this.getConstants().pipe(
      tap(response => {
        this.constant = response;
        if (this.platformService.isBrowser()) {
          this.platformService.getNativeLocalStorage()?.setItem('constants', JSON.stringify(this.constant));
        }
      })
    );
  }

  getJssCategoryHomepageFirstHighlighted() {
    return this.constant.jssCategoryHomepageFirstHighlighted;
  }

  getJssCategoryHomepageSecondHighlighted() {
    return this.constant.jssCategoryHomepageSecondHighlighted;
  }

  getJssCategoryHomepageThirdHighlighted() {
    return this.constant.jssCategoryHomepageThirdHighlighted;
  }

  getCategoryInterview() {
    return this.constant.categoryInterview;
  }

  getCategoryPodcast() {
    return this.constant.categoryPodcast;
  }

  getCategoryArticle() {
    return this.constant.categoryArticle;
  }

  getCategorySerie() {
    return this.constant.categorySerie;
  }

  getCategoryExclusivity() {
    return this.constant.categoryExclusivity;
  }

}

@Injectable({ providedIn: 'root' })
export class ConstantsResolver implements Resolve<any> {
  constructor(private constantsService: ConstantService) { }

  resolve(): Observable<any> {
    return this.constantsService.initConstantFromResolver();
  }
}
