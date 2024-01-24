import { AfterContentChecked, Component, OnInit, } from '@angular/core';

@Component({
  selector: 'visit-prepa-tiers-turnover',
  templateUrl: './visit-prepa-tiers-turnover.component.html',
  styleUrls: ['./visit-prepa-tiers-turnover.component.css']
})

export class VisitPrepaTiersTurnoverComponent implements OnInit, AfterContentChecked {

  constructor(
  ) { }

  ngAfterContentChecked(): void {
    throw new Error('Method not implemented.');
  }

  ngOnInit() {

  }
}


