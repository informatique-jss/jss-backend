import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/services/app.service';
import { UserPreferenceService } from 'src/app/services/user.preference.service';

@Component({
  selector: 'app-crm',
  templateUrl: './crm.component.html',
  styleUrls: ['./crm.component.css']
})
export class CrmComponent implements OnInit {

  constructor(private appService: AppService,
    private userPreferenceService: UserPreferenceService

  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("CRM");
  }
}
