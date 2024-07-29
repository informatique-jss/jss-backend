import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { VatCollectionType } from '../../../model/VatCollectionType';
import { VatCollectionTypeService } from '../../../services/vat.collection.type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'select-vat-collection-type',
  templateUrl: './select-vat-collection-type.component.html',
  styleUrls: ['./select-vat-collection-type.component.css']
})
export class SelectVatCollectionTypeComponent extends GenericSelectComponent<VatCollectionType> implements OnInit {

  types: VatCollectionType[] = [] as Array<VatCollectionType>;

  constructor(private formBuild: UntypedFormBuilder, private vatCollectionTypeService: VatCollectionTypeService, private appService3: AppService) {
    super(formBuild, appService3)
  }

  initTypes(): void {
    this.vatCollectionTypeService.getVatCollectionTypes().subscribe(response => {
      this.types = response;
    })
  }
}
