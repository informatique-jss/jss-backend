import { Component, OnInit } from '@angular/core';
import { SHARED_IMPORTS } from '../../../libs/SharedImports';

@Component({
  selector: 'contribute',
  templateUrl: './contribute.component.html',
  styleUrls: ['./contribute.component.css'],
  standalone: true,
  imports: [SHARED_IMPORTS]
})
export class ContributeComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
