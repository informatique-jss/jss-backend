import { Component, EventEmitter, OnInit, Output } from '@angular/core';

export const TYPE_CHOSEN_QUOTATION: string = "TYPE_CHOSEN_QUOTATION";
export const TYPE_CHOSEN_ORDER: string = "TYPE_CHOSEN_ORDER";

@Component({
  selector: 'choose-type',
  templateUrl: './choose-type.component.html',
  styleUrls: ['./choose-type.component.css']
})

export class ChooseTypeComponent implements OnInit {

  TYPE_CHOSEN_QUOTATION = TYPE_CHOSEN_QUOTATION;
  TYPE_CHOSEN_ORDER = TYPE_CHOSEN_ORDER;

  @Output() onChoseType = new EventEmitter<string>();

  constructor() { }

  ngOnInit() {
  }

  choseType(typeChosen: string) {
    this.onChoseType.emit(typeChosen);
  }

}
