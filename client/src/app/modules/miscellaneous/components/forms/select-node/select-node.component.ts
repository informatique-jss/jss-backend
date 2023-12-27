import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { Node } from 'src/app/modules/monitoring/model/Node';
import { NodeService } from 'src/app/modules/monitoring/services/node.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { GenericMultipleSelectComponent } from '../generic-select/generic-multiple-select.component';

@Component({
  selector: 'select-node',
  templateUrl: './../generic-select/generic-multiple-select.component.html',
  styleUrls: ['./../generic-select/generic-select.component.html']
})

export class SelectNodeComponent extends GenericMultipleSelectComponent<Node> implements OnInit {

  types: Node[] = [] as Array<Node>;

  constructor(private formBuild: UntypedFormBuilder,
    private nodeService: NodeService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.nodeService.getNodes().subscribe(response => {
      this.types = response;
    });
  }
}
