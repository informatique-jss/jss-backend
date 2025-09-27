import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';

@Component({
  selector: 'not-found-page',
  templateUrl: './not.found.page.component.html',
  styleUrls: ['./not.found.page.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class NotFoundPageComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }
}
