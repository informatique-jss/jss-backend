import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { GUICHET_UNIQUE_STATUS_VALIDATION_PENDING } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { FormaliteGuichetUnique } from '../model/guichet-unique/FormaliteGuichetUnique';
import { ValidationRequest } from '../model/guichet-unique/ValidationRequest';

@Component({
  selector: 'guichet-unique-status',
  templateUrl: './guichet-unique-status.component.html',
  styleUrls: ['./guichet-unique-status.component.css']
})
export class GuichetUniqueStatusComponent implements OnInit {

  @Input() formalitesGuichetUnique: FormaliteGuichetUnique[] | undefined;
  GUICHET_UNIQUE_STATUS_VALIDATION_PENDING = GUICHET_UNIQUE_STATUS_VALIDATION_PENDING;
  selectedIndex: number[] = [];
  constructor() { }

  ngOnInit() {
    this.orderValidationRequests();
  }

  ngOnChanges(changes: SimpleChanges) {
    this.orderValidationRequests();
  }

  orderValidationRequests() {
    if (this.formalitesGuichetUnique) {
      for (let j = 0; j < this.formalitesGuichetUnique.length; j++) {
        let formaliteGuichetUnique = this.formalitesGuichetUnique[j];
        if (formaliteGuichetUnique.validationsRequests) {
          formaliteGuichetUnique.validationsRequests.sort((a: ValidationRequest, b: ValidationRequest) => {
            if (new Date(a.statusDate).getTime() == new Date(b.statusDate).getTime()) return 0;
            return new Date(a.statusDate).getTime() > new Date(b.statusDate).getTime() ? 1 : -1;
          })
        }
        for (let i = 0; i < formaliteGuichetUnique.formaliteStatusHistoryItems.length; i++)
          if (formaliteGuichetUnique.formaliteStatusHistoryItems[i].status.code == GUICHET_UNIQUE_STATUS_VALIDATION_PENDING) {
            this.selectedIndex[j] = i;
          }
      }
    }
  }

  getFormaliteLabel(formalite: FormaliteGuichetUnique): string {
    let date = "";
    if (formalite && formalite.created)
      date = formatDateFrance(new Date(formalite.created));

    let out = "";

    if (formalite.referenceMandataire)
      out += formalite.referenceMandataire + " - ";

    out += formalite.liasseNumber + " - " + date;
    return out;
  }
}
