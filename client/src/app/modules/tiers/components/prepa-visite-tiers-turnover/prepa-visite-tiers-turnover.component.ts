import { AfterContentChecked, Component, OnInit, } from '@angular/core';

@Component({
  selector: 'prepa-visite-tiers-turnover',
  templateUrl: './prepa-visite-tiers-turnover.component.html',
  styleUrls: ['./prepa-visite-tiers-turnover.component.css']
})

export class PrepaVisiteTiersTurnoverComponent implements OnInit, AfterContentChecked {

  constructor(
  ) { }

  ngAfterContentChecked(): void {
    throw new Error('Method not implemented.');
  }

  ngOnInit() {

  }
}


