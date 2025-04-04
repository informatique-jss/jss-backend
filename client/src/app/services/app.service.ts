import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { BehaviorSubject } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private title: BehaviorSubject<string> = new BehaviorSubject<string>("title");
  titleObservable = this.title.asObservable();

  private save: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  saveObservable = this.save.asObservable();

  private sidenavOpenState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  sidenavOpenStateObservable = this.sidenavOpenState.asObservable();

  constructor(
    private snackBar: MatSnackBar,
    private router: Router
  ) { }

  changeHeaderTitle(title: string) {
    this.title.next(title);
  }

  changeSidenavOpenState(sidenavOpenState: boolean) {
    this.sidenavOpenState.next(sidenavOpenState);
  }

  triggerSaveEvent() {
    this.save.next(true);
  }

  displaySnackBar(message: string, isError: boolean, duration: number) {
    let sb = this.snackBar.open(message, 'Fermer', {
      duration: duration * 1000, panelClass: [isError ? "red-snackbar" : "blue-snackbar"]
    });
    sb.onAction().subscribe(() => {
      sb.dismiss();
    });
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
      this.router.navigateByUrl('/', { skipLocationChange: true }).then(() =>
        this.router.navigate(['/' + route])
      );
      if (sameWindowEndFonction)
        sameWindowEndFonction();
    }
    return;
  }

  /**
   * Open given route to MyJss website on a new tab
   * @param route  : route to open
   * @returns
   */
  openMyJssRoute(route: string) {
    window.open(environment.frontendMyJSSUrl + route, "_blank");
    return;
  }

}
