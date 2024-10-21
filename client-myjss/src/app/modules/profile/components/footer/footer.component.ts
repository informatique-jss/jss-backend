import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'main-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.css']
})
export class FooterComponent implements OnInit {
  logoJss: string = '/assets/images/logo.png';

  constructor(
    private appService: AppService
  ) { }

  ngOnInit() {
  }

  openPage(page: string, event: any) {
    this.appService.openRoute(event, page + "/", undefined);
  }
}
