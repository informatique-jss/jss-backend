import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';

@Component({
  selector: 'app-accounting',
  templateUrl: './accounting.component.html',
  styleUrls: ['./accounting.component.css']
})
export class AccountingComponent implements OnInit {

  constructor(private appService: AppService,
    private habilitationService: HabilitationsService,
  ) { }

  canDisplayProfitLost() {
    this.habilitationService.canDisplayProfitLost();
  }

  canDisplayBilan() {
    this.habilitationService.canDisplayBilan();
  }

  ngOnInit() {
    this.appService.changeHeaderTitle("Comptabilité");
  }
}
