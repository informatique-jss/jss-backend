import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'contact-us',
  templateUrl: './contact-us.component.html',
  styleUrls: ['./contact-us.component.css']
})
export class ContactUsComponent implements OnInit {

  constructor(
    private appService: AppService
  ) { }

  ngOnInit() {
  }


  openCgv(event: any) {
    this.appService.openRoute(event, 'cgv', undefined);
  }
}
