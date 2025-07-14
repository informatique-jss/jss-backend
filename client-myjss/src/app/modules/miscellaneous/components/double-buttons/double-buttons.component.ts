import { Component, Input, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';
import { AppService } from '../../../main/services/app.service';

@Component({
  selector: 'double-buttons',
  templateUrl: './double-buttons.component.html',
  styleUrls: ['./double-buttons.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class DoubleButtonsComponent implements OnInit {
  @Input() orderActionLabel: string = "";
  @Input() orderActionRoute: string = "";
  @Input() quotationActionLabel: string = "";
  @Input() quotationActionRoute: string = "";
  @Input() linkLabel: string = "";
  @Input() linkRoute: string = "";
  @Input() linkRouteToJssMedia: boolean = false;
  @Input() isLightButtons: boolean = true;
  constructor(private appService: AppService) { }

  ngOnInit() {
  }

  openRoute(event: any, route: string) {
    this.appService.openRoute(event, route, undefined);
  }

  openRouteForLink(event: any, route: string) {
    if (this.linkRouteToJssMedia) {
      this.appService.openJssRoute(event, route, undefined);
    } else
      this.openRoute(event, route);
  }
}
