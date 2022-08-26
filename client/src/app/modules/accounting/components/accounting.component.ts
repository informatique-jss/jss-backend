import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'app-accounting',
  templateUrl: './accounting.component.html',
  styleUrls: ['./accounting.component.css']
})
export class AccountingComponent implements OnInit {

  constructor(private appService: AppService,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Comptabilité");
  }

}
