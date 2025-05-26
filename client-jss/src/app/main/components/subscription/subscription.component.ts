import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';

@Component({
  selector: 'subscription',
  templateUrl: './subscription.component.html',
  styleUrls: ['./subscription.component.css'],
  imports: [SHARED_IMPORTS],
  standalone: true
})
export class SubscriptionComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
