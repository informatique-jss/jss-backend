import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Civility } from '../../../model/Civility';
import { CivilityService } from '../../../services/civility.service';
import { GenericRadioGroupComponent } from '../generic-radio-group/generic-radio-group.component';

@Component({
  selector: 'radio-group-civility',
  templateUrl: '../generic-radio-group/generic-radio-group-code.component.html',
  styleUrls: ['../generic-radio-group/generic-radio-group.component.css']
})
export class RadioGroupCivilityComponent extends GenericRadioGroupComponent<Civility> implements OnInit {
  types: Civility[] = [] as Array<Civility>;

  constructor(
    private formBuild: UntypedFormBuilder, private civilityService: CivilityService) {
    super(formBuild);
  }

  initTypes(): void {
    this.civilityService.getCivilities().subscribe(response => {
      this.types = response;
    })
  }
}
