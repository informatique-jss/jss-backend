import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';

@Component({
  selector: 'not-found-page',
  templateUrl: './not.found.page.component.html',
  styleUrls: ['./not.found.page.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class NotFoundPageComponent implements OnInit {

  constructor(private appService: AppService) { }

  ngOnInit() {
  }

  goToHomePage(event: any) {
    this.appService.openRoute(event, "/home", undefined);
  }

}
