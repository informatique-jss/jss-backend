import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { FormaliteInfogreffe } from '../../model/infogreffe/FormaliteInfogreffe';
import { EvenementInfogreffe } from '../../model/infogreffe/EvenementInfogreffe';
import { INFOGREFFE_STATUS_SENDING_PENDING } from 'src/app/libs/Constants';

@Component({
  selector: 'infogreffe-status',
  templateUrl: './infogreffe-status.component.html',
  styleUrls: ['./infogreffe-status.component.css']
})
export class InfogreffeStatusComponent implements OnInit {

  @Input() formalitesInfogreffe: FormaliteInfogreffe[] | undefined;

  constructor() { }

  ngOnInit() {
    this.orderValidationRequests();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.orderValidationRequests();
  }

  orderValidationRequests() {
    if (this.formalitesInfogreffe) {
      for (let j = 0; j < this.formalitesInfogreffe.length; j++) {
        let formaliteInfogreffe = this.formalitesInfogreffe[j];
        if (formaliteInfogreffe.evenements) {
          formaliteInfogreffe.evenements.sort((a: EvenementInfogreffe, b: EvenementInfogreffe) => {
            if (new Date(a.date).getTime() == new Date(b.date).getTime()) return 0;
            return new Date(a.date).getTime() > new Date(b.date).getTime() ? 1 : -1;
          })
        }
      }
    }
  }

}
