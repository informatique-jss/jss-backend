import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { VatCollectionType } from '../../../model/VatCollectionType';
import { VatCollectionTypeService } from '../../../services/vat.collection.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';

@Component({
  selector: 'select-vat-collection-type',
  templateUrl: './select-vat-collection-type.component.html',
  styleUrls: ['./select-vat-collection-type.component.css']
})
export class SelectVatCollectionTypeComponent extends GenericSelectComponent<VatCollectionType> implements OnInit {

  types: VatCollectionType[] = [] as Array<VatCollectionType>;

  constructor(private formBuild: UntypedFormBuilder, private vatCollectionTypeService: VatCollectionTypeService,) {
    super(formBuild)
  }

  initTypes(): void {
    this.vatCollectionTypeService.getVatCollectionTypes().subscribe(response => {
      this.types = response;
    })
  }
}
