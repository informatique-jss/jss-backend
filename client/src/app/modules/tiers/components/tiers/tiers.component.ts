import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/app.service';
import { Tiers } from '../../model/Tiers';

@Component({
  selector: 'app-tiers',
  templateUrl: './tiers.component.html',
  styleUrls: ['./tiers.component.css']
})
export class TiersComponent implements OnInit {

  tiers: Tiers = {} as Tiers;

  constructor(private appService: AppService) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Tiers / Responsables");
  }

}
