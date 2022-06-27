import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { JournalType } from 'src/app/modules/quotation/model/JournalType';
import { JournalTypeService } from 'src/app/modules/quotation/services/journal.type.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-journal-type',
  templateUrl: './../generic-referential/generic-referential.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialJournalTypeComponent extends GenericReferentialComponent<JournalType> implements OnInit {
  constructor(private journalTypeService: JournalTypeService,
    private formBuilder2: FormBuilder) {
    super(formBuilder2);
  }

  getAddOrUpdateObservable(): Observable<JournalType> {
    return this.journalTypeService.addOrUpdateJournalType(this.selectedEntity!);
  }
  getGetObservable(): Observable<JournalType[]> {
    return this.journalTypeService.getJournalTypes();
  }
}
