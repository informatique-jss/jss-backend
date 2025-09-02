import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TendencyHubComponent } from "../last-posts-hub/last-posts-hub.component";

@Component({
  selector: 'post-last-header',
  templateUrl: './post-last-header.component.html',
  imports: [SHARED_IMPORTS, TendencyHubComponent],
  standalone: true
})
export class PostLastHeaderComponent implements OnInit {

  constructor(
  ) { }


  ngOnInit() {

  }


}
