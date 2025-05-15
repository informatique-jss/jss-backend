import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'my-indicators',
  templateUrl: './my-indicators.component.html',
  styleUrls: ['./my-indicators.component.css']
})
export class MyIndicatorsComponent implements OnInit {

  constructor(private appService: AppService) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Mes indicateurs");
  }

}
