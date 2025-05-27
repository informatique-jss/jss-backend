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
  @Input() quotationActionLabel: string = "";
  @Input() linkLabel: string = "";
  @Input() isLightButtons: boolean = true;
  constructor(private appService: AppService) { }

  ngOnInit() {
  }
  openRoute(event: any) {
    this.appService.openRoute(event, '', undefined);
  }
}
