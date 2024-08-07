import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericChipsComponent } from '../generic-chips/generic-chips.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'generic-chips-input',
  templateUrl: './../generic-chips/generic-chips.component.html',
  styleUrls: ['./../generic-chips/generic-chips.component.css']
})
export class GenericChipsInputComponent extends GenericChipsComponent<string> implements OnInit {

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService) {
    super(formBuild, appService3)
  }

  validateInput(value: string): boolean {
    return true;
  }

  setValueToObject(value: string, object: string): string {
    object = value;
    return object;
  }

  getValueFromObject(object: string): string {
    return object;
  }

  callOnNgInit(): void {
  }
}
