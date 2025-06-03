import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { TendencyHubComponent } from '../tendency-hub/tendency-hub.component';

@Component({
  selector: 'post-tendency-header',
  templateUrl: './post-tendency-header.component.html',
  styleUrls: ['./post-tendency-header.component.css'],
  imports: [SHARED_IMPORTS, TendencyHubComponent],
  standalone: true
})
export class PostTendencyHeaderComponent implements OnInit {

  constructor(
  ) { }


  ngOnInit() {

  }


}
