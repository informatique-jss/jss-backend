import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericFormComponent } from '../generic-form.components';

@Component({
  selector: 'generic-checkbox',
  templateUrl: './generic-checkbox.component.html',
  styleUrls: ['./generic-checkbox.component.css']
})
export class GenericCheckboxComponent extends GenericFormComponent implements OnInit {
  constructor(
    private formBuilder3: UntypedFormBuilder,) {
    super(formBuilder3);
  }

  callOnNgInit(): void {
  }

}
