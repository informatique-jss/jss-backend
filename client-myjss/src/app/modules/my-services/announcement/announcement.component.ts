import { Component, OnInit } from '@angular/core';
import { AppService } from '../../../libs/app.service';

@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {

  orderActionLabel: string = "Publier une annonce légale";
  quotationActionLabel: string = "Demander un devis";
  pathAnnouncementSearch: string = "announcement/search";
  linkLabel: string = "Consultez une annonce légale";

  constructor(private appService: AppService) {
  }

  ngOnInit() {
  }
  openProduct(event: any) {
    this.appService.openRoute(event, "product/" + "", undefined);
  }
  openServices(event: any) {
    this.appService.openRoute(event, "my-services/" + "", undefined);
  }
}
