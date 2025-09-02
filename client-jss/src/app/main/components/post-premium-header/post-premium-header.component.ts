import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { PremiumHubComponent } from "../premium-hub/premium-hub.component";

@Component({
  selector: 'post-tendency-header',
  templateUrl: './post-premium-header.component.html',
  imports: [SHARED_IMPORTS, PremiumHubComponent],
  standalone: true
})
export class PostPremiumHeaderComponent implements OnInit {

  constructor() { }

  ngOnInit() { }
}
