import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Resolve } from '@angular/router';
import { Observable, of, tap } from 'rxjs';
import { Constant, globalConstantCache } from '../model/Constant';
import { AppRestService } from './appRest.service';

@Injectable({
  providedIn: 'root',
})
export class ConstantService extends AppRestService<Constant> {

  constructor(http: HttpClient,) {
    super(http, "quotation");
  }

  constant: Constant = {} as Constant;

  getConstants() {
    return this.get(new HttpParams(), "constants");
  }

  initConstant() {
    if (this.constant && this.constant.id) {
      return;
    }

    let a = localStorage.getItem('constants');
    this.constant = JSON.parse(a!) as Constant;

    this.getConstants().subscribe(response => {
      this.constant = response;
      globalConstantCache.data = response;
      localStorage.setItem('constants', JSON.stringify(this.constant));
    });
  }

  initConstantFromResolver(): Observable<Constant> {
    if (this.constant && this.constant.id) {
      return of(this.constant);
    }

    const stored = localStorage.getItem('constants');
    if (stored) {
      this.constant = JSON.parse(stored) as Constant;
      return of(this.constant);
    }

    return this.getConstants().pipe(
      tap(response => {
        this.constant = response;
        globalConstantCache.data = response;
        localStorage.setItem('constants', JSON.stringify(this.constant));
      })
    );
  }
}

@Injectable({ providedIn: 'root' })
export class ConstantsResolver implements Resolve<any> {
  constructor(private constantsService: ConstantService) { }

  resolve(): Observable<any> {
    return this.constantsService.initConstantFromResolver();
  }
}
