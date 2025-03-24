import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'app-offers',
  templateUrl: './offers.component.html',
  styleUrls: ['./offers.component.css']
})
export class OffersComponent implements OnInit {

  constructor(private appService: AppService

  ) { }

  ngOnInit(
  ) {
  }

  openProduct(event: any) {
    this.appService.openRoute(event, "product/" + "", undefined);
  }
  openServices(event: any) {
    this.appService.openRoute(event, "my-services/" + "", undefined);
  }
}
