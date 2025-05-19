import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-string',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
  standalone: false
})
export class SelectStringComponent extends GenericSelectComponent<string> implements OnInit {

  @Input() types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder) {
    super(formBuild)
  }

  override initTypes(): void {
    this.types = this.types;
  }

  override compareWithId = (o1: any, o2: any) => {
    return o1 == o2;
  }

}
