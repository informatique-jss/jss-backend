import { Component } from '@angular/core';
import { FormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Subscription } from 'rxjs';
import { AppService } from './app.service';

export class CustomErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: FormControl | null, form: FormGroupDirective | NgForm | null): boolean {
    const isSubmitted = form && form.submitted;
    return !!(control && control.invalid && (control.dirty || control.touched || isSubmitted));
  }
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {
  title: string = 'JSS Backend';

  sidenavOpenState: boolean = true;
  sidenavOpenStateSubscription: Subscription = new Subscription;

  constructor(protected appService: AppService) { }
  groups: string[] = [] as Array<string>;

  ngOnInit() {
    this.sidenavOpenStateSubscription = this.appService.sidenavOpenStateObservable.subscribe(item => this.sidenavOpenState = item);
  }

  ngOnDestroy() {
    this.sidenavOpenStateSubscription.unsubscribe();
  }
}
