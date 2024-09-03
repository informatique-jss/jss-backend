import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { Dictionnary } from 'src/app/libs/Dictionnary';
import { EvenementInfogreffe } from '../../model/infogreffe/EvenementInfogreffe';
import { FormaliteInfogreffe } from '../../model/infogreffe/FormaliteInfogreffe';

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
            if (new Date(a.createdDate).getTime() == new Date(b.createdDate).getTime()) return 0;
            return new Date(a.createdDate).getTime() > new Date(b.createdDate).getTime() ? 1 : -1;
          })
        }
      }
    }
  }

  getFieldLabel(fieldName: string) {
    let dictionnary = Dictionnary as any;
    if (dictionnary[fieldName])
      return dictionnary[fieldName];
    return fieldName;
  }

  getFormaliteLabel(formalite: FormaliteInfogreffe): string {
    let out = "";
    if (formalite) {
      if (formalite.referenceClient && formalite.referenceTechnique)
        out += formalite.referenceClient + " - " + formalite.referenceTechnique;
    }
    return out;
  }
}
