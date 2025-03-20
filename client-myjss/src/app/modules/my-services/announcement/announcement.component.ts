import { Component, OnInit } from '@angular/core';
import { jarallax } from 'jarallax';
import { AppService } from '../../../libs/app.service';

@Component({
  selector: 'announcement',
  templateUrl: './announcement.component.html',
  styleUrls: ['./announcement.component.css']
})
export class AnnouncementComponent implements OnInit {

  constructor(private appService: AppService) {
  }

  ngOnInit() {

  }
  ngAfterViewInit(): void {
    jarallax(document.querySelectorAll('.jarallax'), {
      speed: 0.5
    });
  }
  openProduct(event: any) {
    this.appService.openRoute(event, "product/" + "", undefined);
  }
  openServices(event: any) {
    this.appService.openRoute(event, "my-services/" + "", undefined);
  }
}
