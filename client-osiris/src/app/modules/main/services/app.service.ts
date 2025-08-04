import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private loadingSpinner = new BehaviorSubject<boolean>(false);
  readonly loadingSpinnerObservable = this.loadingSpinner.asObservable();

  constructor(
    private router: Router,
  ) { }

  showLoadingSpinner(): void {
    this.loadingSpinner.next(true);
  }

  hideLoadingSpinner(): void {
    this.loadingSpinner.next(false);
  }

  displayToast(message: string, isError: boolean, title: string, delayInMili: number) {
    // TODO
  }

  /**
   * Open given route taking account of user action to do it (simple click, ctrl + click, middle click)
   * @param event : click event given by angular
   * @param route  : route to open
   * @param sameWindowEndFonction : function to execute at the end of open with simple click
   * @returns
   */
  openRoute(event: any, route: string, sameWindowEndFonction: any) {
    if (event && (event.ctrlKey || event.button && event.button == "1")) {
      window.open(location.origin + "/" + route, "_blank");
    } else {
      this.router.navigate(['/' + route])
      if (sameWindowEndFonction)
        sameWindowEndFonction();
    }
    return;
  }

}
