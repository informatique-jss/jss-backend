import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TendencyHubComponent } from "../last-posts-hub/last-posts-hub.component";

@Component({
  selector: 'post-most-seen-header',
  templateUrl: './post-most-seen-header.component.html',
  imports: [SHARED_IMPORTS, TendencyHubComponent],
  standalone: true
})
export class PostMostSeenHeaderComponent implements OnInit {

  constructor(
  ) { }


  ngOnInit() {

  }


}
