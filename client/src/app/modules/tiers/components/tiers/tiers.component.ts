import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { AppService } from 'src/app/app.service';
import { Tiers } from '../../model/Tiers';
import { TiersService } from '../../services/tiers.service';

@Component({
  selector: 'app-tiers',
  templateUrl: './tiers.component.html',
  styleUrls: ['./tiers.component.css']
})
export class TiersComponent implements OnInit {

  tiers: Tiers = {} as Tiers;
  eventsTiersLoaded: Subject<void> = new Subject<void>();

  constructor(private appService: AppService,
    private tiersService: TiersService) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Tiers / Responsables");

    this.tiersService.getTiers(215).subscribe(response => {
      this.tiers = response;
      this.eventsTiersLoaded.next();
    })
  }

}
