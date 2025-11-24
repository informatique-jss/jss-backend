import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { Toast } from '../../../libs/toast/Toast';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private loadingSpinner = new BehaviorSubject<boolean>(false);
  readonly loadingSpinnerObservable = this.loadingSpinner.asObservable();

  toasts: Toast[] = [];
  private toastSource = new BehaviorSubject<Toast[]>(this.toasts);

  constructor(
    private router: Router,
  ) { }

  showLoadingSpinner(): void {
    this.loadingSpinner.next(true);
  }

  hideLoadingSpinner(): void {
    this.loadingSpinner.next(false);
  }

  displayToast(message: string, isError: boolean, title: string, delayInMili?: number) {
    if (!delayInMili) delayInMili = 5000;
    let newToast = { isError: isError, message: message, title: title, delay: delayInMili } as Toast;
    this.toasts.push(newToast);
    this.toastSource.next(this.toasts);
    if (this.toasts.indexOf(newToast) >= 0)
      setTimeout(() => {
        this.toasts.splice(this.toasts.indexOf(newToast), 1);
        this.toastSource.next(this.toasts);
      }
        , delayInMili);
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
