import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { AppService } from 'src/app/services/app.service';
import { HabilitationsService } from '../../../../services/habilitations.service';
import { UserPreferenceService } from '../../../../services/user.preference.service';

@Component({
  selector: 'app-accounting',
  templateUrl: './accounting.component.html',
  styleUrls: ['./accounting.component.css']
})
export class AccountingComponent implements OnInit {

  constructor(private appService: AppService,
    private habilitationService: HabilitationsService,
    private userPreferenceService: UserPreferenceService,
  ) { }

  canDisplayProfitLost() {
    return this.habilitationService.canDisplayProfitLost();
  }

  canDisplayFae() {
    return this.habilitationService.canDisplayFae();
  }

  canDisplayFnp() {
    return this.habilitationService.canDisplayFnp();
  }

  canDisplayBilan() {
    return this.habilitationService.canDisplayBilan();
  }

  ngOnInit() {
    this.appService.changeHeaderTitle("Comptabilité");
    this.restoreTab();
  }


  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('accounting', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('accounting');
  }
}
