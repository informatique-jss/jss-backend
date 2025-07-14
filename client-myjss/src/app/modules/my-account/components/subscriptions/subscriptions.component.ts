import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../../libs/SharedImports';

@Component({
  selector: 'subscriptions',
  templateUrl: './subscriptions.component.html',
  styleUrls: ['./subscriptions.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class SubscriptionsComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
