import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'not-found-page',
  templateUrl: './not.found.page.component.html',
  styleUrls: ['./not.found.page.component.css'],
  standalone: false
})
export class NotFoundPageComponent implements OnInit {

  constructor(private appService: AppService) { }

  ngOnInit() {
  }

  goToHomePage(event: any) {
    this.appService.openRoute(event, "/home", undefined);
  }

}
