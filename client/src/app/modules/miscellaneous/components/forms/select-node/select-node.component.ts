import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Node } from 'src/app/modules/monitoring/model/Node';
import { NodeService } from 'src/app/modules/monitoring/services/node.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-node',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectNodeComponent extends GenericMultipleSelectComponent<Node> implements OnInit {

  types: Node[] = [] as Array<Node>;

  constructor(private formBuild: UntypedFormBuilder,
    private nodeService: NodeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.nodeService.getNodes().subscribe(response => {
      this.types = response;
    });
  }

  displayLabel(object: any): string {
    if (object && object.hostname)
      return object.hostname;
    return super.displayLabel(object);
  }
}
