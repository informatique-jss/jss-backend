import { Component, OnInit } from '@angular/core';
import { MatTabChangeEvent } from '@angular/material/tabs';
import { ActivatedRoute, UrlSegment } from '@angular/router';
import { UserPreferenceService } from 'src/app/services/user.preference.service';

@Component({
  selector: 'indicator',
  templateUrl: './indicator.component.html',
  styleUrls: ['./indicator.component.css']
})
export class IndicatorComponent implements OnInit {

  constructor(private userPreferenceService: UserPreferenceService,
    private activatedRoute: ActivatedRoute,
  ) { }

  idIndicator: number | undefined;
  idEmployee: number | undefined;

  ngOnInit() {
    let url: UrlSegment[] = this.activatedRoute.snapshot.url;
    if (url != undefined && url != null && url[1] != undefined && url[1].path == "detailed") {
      this.index = 1;
      this.idIndicator = this.activatedRoute.snapshot.params.idIndicator;
      this.idEmployee = this.activatedRoute.snapshot.params.idEmployee;
    } else {
      this.restoreTab();
    }
  }

  //Tabs management
  index: number = 0;
  onTabChange(event: MatTabChangeEvent) {
    this.userPreferenceService.setUserTabsSelectionIndex('indicator', event.index);
  }

  restoreTab() {
    this.index = this.userPreferenceService.getUserTabsSelectionIndex('indicator');
  }

}
