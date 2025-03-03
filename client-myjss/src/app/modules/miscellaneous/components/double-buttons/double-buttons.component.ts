import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'double-buttons',
  templateUrl: './double-buttons.component.html',
  styleUrls: ['./double-buttons.component.css']
})
export class DoubleButtonsComponent implements OnInit {
  @Input() orderActionLabel: string = "";
  @Input() quotationActionLabel: string = "";
  @Input() linkLabel: string = "";
  constructor() { }

  ngOnInit() {
  }
  openRoute(event: any) {

  }
}
