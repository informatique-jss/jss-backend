import { Component, Input, OnInit, SimpleChanges } from '@angular/core';
import { combineLatest } from 'rxjs';
import { GUICHET_UNIQUE_STATUS_VALIDATION_PENDING } from 'src/app/libs/Constants';
import { formatDateFrance } from 'src/app/libs/FormatHelper';
import { FormaliteGuichetUniqueService } from '../../../miscellaneous/services/formalite.guichet.unique.service';
import { FormaliteGuichetUnique } from '../../model/guichet-unique/FormaliteGuichetUnique';
import { ValidationRequest } from '../../model/guichet-unique/ValidationRequest';

@Component({
  selector: 'guichet-unique-status',
  templateUrl: './guichet-unique-status.component.html',
  styleUrls: ['./guichet-unique-status.component.css']
})
export class GuichetUniqueStatusComponent implements OnInit {

  @Input() formalitesGuichetUniqueIn: FormaliteGuichetUnique[] | undefined;
  formalitesGuichetUnique: FormaliteGuichetUnique[] | undefined;
  GUICHET_UNIQUE_STATUS_VALIDATION_PENDING = GUICHET_UNIQUE_STATUS_VALIDATION_PENDING;
  selectedIndex: number[] = [];
  constructor(
    private formaliteGuichetUniqueService: FormaliteGuichetUniqueService
  ) { }

  ngOnInit() {
    let promises = [];
    if (this.formalitesGuichetUniqueIn) {
      for (let formalite of this.formalitesGuichetUniqueIn)
        promises.push(this.formaliteGuichetUniqueService.getFormaliteGuichetUniqueService(formalite.id));

      combineLatest(promises).subscribe(response => {
        this.formalitesGuichetUnique = response;
        this.orderValidationRequests();
      })
    }
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
