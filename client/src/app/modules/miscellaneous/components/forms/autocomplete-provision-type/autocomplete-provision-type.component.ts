import { Component, OnInit, SimpleChanges } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { ProvisionType } from 'src/app/modules/quotation/model/ProvisionType';
import { ProvisionTypeService } from 'src/app/modules/quotation/services/provision.type.service';
import { UserNoteService } from 'src/app/services/user.notes.service';
import { ConstantService } from '../../../services/constant.service';
import { GenericLocalAutocompleteComponent } from '../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-provision-type',
  templateUrl: '../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../generic-local-autocomplete/generic-local-autocomplete.component.css'],
})
export class AutocompleteProvisionTypeComponent extends GenericLocalAutocompleteComponent<ProvisionType> implements OnInit {

  types: ProvisionType[] = [] as Array<ProvisionType>;

  constructor(private formBuild: UntypedFormBuilder, private provisionTypeService: ProvisionTypeService,
    private constantService: ConstantService,
    private userNoteService2: UserNoteService,) {
    super(formBuild, userNoteService2)
  }

  ngOnChanges(changes: SimpleChanges): void {
    super.ngOnChanges(changes);
  }

  filterEntities(types: ProvisionType[], value: string): ProvisionType[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(provisionType =>
      provisionType.label != undefined && (provisionType.label.toLowerCase().includes(filterValue))
      || provisionType.code != undefined && (provisionType.code.toLowerCase().includes(filterValue)));
  }

  initTypes(): void {
    this.provisionTypeService.getProvisionTypes().subscribe(response => {
      this.types = response;
    });
  }

  displayLabel(object: ProvisionType): string {
    if (object && object.label)
      return object.code + ' - ' + object.label;
    if (typeof object === "string")
      return object;
    return "";
  }
}
