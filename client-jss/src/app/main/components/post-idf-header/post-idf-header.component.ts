import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';
import { IdfHubComponent } from '../idf-hub/idf-hub.component';

@Component({
  selector: 'post-idf-header',
  templateUrl: './post-idf-header.component.html',
  styleUrls: ['./post-idf-header.component.css'],
  imports: [SHARED_IMPORTS, IdfHubComponent],
  standalone: true
})
export class PostIdfHeaderComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
