import { Component, OnInit } from "@angular/core";
import { UntypedFormBuilder } from "@angular/forms";
import { AssignationType } from "src/app/modules/quotation/model/AssignationType";
import { AssignationTypeService } from "src/app/modules/quotation/services/assignation.type.service";
import { GenericSelectComponent } from "../generic-select/generic-select.component";
import { AppService } from "src/app/services/app.service";

@Component({
  selector: 'select-assignation-type',
  templateUrl: './select-assignation-type.component.html',
  styleUrls: ['./select-assignation-type.component.css']
})
export class SelectAssignationTypeComponent extends GenericSelectComponent<AssignationType> implements OnInit {

  types: AssignationType[] = [] as Array<AssignationType>;

  constructor(private formBuild: UntypedFormBuilder, private assignationTypeService: AssignationTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.assignationTypeService.getAssignationTypes().subscribe(response => {
      this.types = response;
    })
  }
}
