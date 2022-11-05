import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { Civility } from '../../../model/Civility';
import { CivilityService } from '../../../services/civility.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-civility',
  templateUrl: './radio-group-civility.component.html',
  styleUrls: ['./radio-group-civility.component.css']
})
export class RadioGroupCivilityComponent extends GenericRadioGroupComponent<Civility> implements OnInit {
  types: Civility[] = [] as Array<Civility>;

  constructor(
    private formBuild: UntypedFormBuilder, private civilityService: CivilityService, private userNoteService2: UserNoteService) {
    super(formBuild, userNoteService2);
  }

  initTypes(): void {
    this.civilityService.getCivilities().subscribe(response => {
      this.types = response;
      if (response && response.length > 0) {
        this.model = response[0];
        this.modelChange.emit(this.model);
      }
    })
  }
}
