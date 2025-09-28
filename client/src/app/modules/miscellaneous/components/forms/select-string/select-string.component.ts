import { Component, Input, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from 'src/app/services/app.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-string',
  templateUrl: '../generic-select/generic-select.component.html',
  styleUrls: ['../generic-select/generic-select.component.css'],
})
export class SelectStringComponent extends GenericSelectComponent<string> implements OnInit {

  @Input() types: string[] = [] as Array<string>;

  constructor(private formBuild: UntypedFormBuilder, private appService3: AppService) {
    super(formBuild, appService3)
  }

  override initTypes(): void {
    this.types = this.types;
  }

  override compareWithId = (o1: any, o2: any) => {
    return o1 == o2;
  }

}
