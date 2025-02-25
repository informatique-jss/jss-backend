import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'app-product',
  templateUrl: './product.component.html',
  styleUrls: ['./product.component.css']
})
export class ProductComponent implements OnInit {

  constructor(
    private appService: AppService,
  ) { }

  ngOnInit() {
  }

  openOffers(event: any) {
    this.appService.openRoute(event, "offers/" + "", undefined);
  }
}
