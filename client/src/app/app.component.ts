import { Component, HostListener } from '@angular/core';
import { UntypedFormControl, FormGroupDirective, NgForm } from '@angular/forms';
import { ErrorStateMatcher } from '@angular/material/core';
import { Subscription } from 'rxjs';
import { AppService } from './app.service';
import { SEARCH_KEY_CODE } from './libs/Constants';
import { SearchService } from './search.service';

export class CustomErrorStateMatcher implements ErrorStateMatcher {
  isErrorState(control: UntypedFormControl | null, form: FormGroupDirective | NgForm | null): boolean {
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

  constructor(protected appService: AppService,
    protected searchService: SearchService) { }
  groups: string[] = [] as Array<string>;

  ngOnInit() {
    this.sidenavOpenStateSubscription = this.appService.sidenavOpenStateObservable.subscribe(item => this.sidenavOpenState = item);
  }

  ngOnDestroy() {
    this.sidenavOpenStateSubscription.unsubscribe();
  }

  @HostListener('window:keyup', ['$event'])
  keyEvent(event: KeyboardEvent) {
    if (event != undefined && event != null && event.code != null && event != undefined && event.code == SEARCH_KEY_CODE)
      this.searchService.openSearch();
  }

}
