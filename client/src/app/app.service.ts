import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppService {

  private title: BehaviorSubject<string> = new BehaviorSubject<string>("title");
  titleObservable = this.title.asObservable();

  private sidenavOpenState: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(true);
  sidenavOpenStateObservable = this.sidenavOpenState.asObservable();


  constructor(
    private snackBar: MatSnackBar,
  ) { }

  changeHeaderTitle(title: string) {
    this.title.next(title);
  }

  changeSidenavOpenState(sidenavOpenState: boolean) {
    this.sidenavOpenState.next(sidenavOpenState);
  }

  displaySnackBar(message: string, isError: boolean, duration: number) {
    let sb = this.snackBar.open(message, 'Fermer', {
      duration: duration * 1000, panelClass: [isError ? "red-snackbar" : "blue-snackbar"]
    });
    sb.onAction().subscribe(() => {
      sb.dismiss();
    });
  }

}
