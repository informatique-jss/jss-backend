import { Component, Input, OnInit } from '@angular/core';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'double-buttons',
  templateUrl: './double-buttons.component.html',
  styleUrls: ['./double-buttons.component.css']
})
export class DoubleButtonsComponent implements OnInit {
  @Input() orderActionLabel: string = "";
  @Input() quotationActionLabel: string = "";
  @Input() linkLabel: string = "";
  constructor(private appService: AppService) { }

  ngOnInit() {
  }
  openRoute(event: any) {
    this.appService.openRoute(event, '', undefined);
  }
}
