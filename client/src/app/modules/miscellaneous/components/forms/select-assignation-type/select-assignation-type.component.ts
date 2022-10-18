import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder } from "@angular/forms";
import { AssignationType } from "src/app/modules/quotation/model/AssignationType";
import { AssignationTypeService } from "src/app/modules/quotation/services/assignation.type.service";
import { UserNoteService } from "src/app/services/user.notes.service";
import { GenericSelectComponent } from "../generic-select/generic-select.component";

@Component({
  selector: 'select-assignation-type',
  templateUrl: './select-assignation-type.component.html',
  styleUrls: ['./select-assignation-type.component.css']
})
export class SelectAssignationTypeComponent extends GenericSelectComponent<AssignationType> implements OnInit {

  types: AssignationType[] = [] as Array<AssignationType>;

  constructor(private formBuild: UntypedFormBuilder, private assignationTypeService: AssignationTypeService, private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  initTypes(): void {
    this.assignationTypeService.getAssignationTypes().subscribe(response => {
      this.types = response;
    })
  }
}
