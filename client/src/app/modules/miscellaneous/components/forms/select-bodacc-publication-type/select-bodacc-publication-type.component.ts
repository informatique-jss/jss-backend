import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { BodaccPublicationType } from 'src/app/modules/quotation/model/BodaccPublicationType';
import { BodaccPublicationTypeService } from 'src/app/modules/quotation/services/bodacc-publication-type.service';
import { GenericSelectComponent } from '../generic-select/generic-select.component';


@Component({
  selector: 'select-bodacc-publication-type',
  templateUrl: './select-bodacc-publication-type.component.html',
  styleUrls: ['./select-bodacc-publication-type.component.css']
})
export class SelectBodaccPublicationTypeComponent extends GenericSelectComponent<BodaccPublicationType> implements OnInit {

  types: BodaccPublicationType[] = [] as Array<BodaccPublicationType>;

  constructor(private changeDetectorRef: ChangeDetectorRef,
    private formBuild: UntypedFormBuilder, private bodaccPublicationTypeService: BodaccPublicationTypeService) {
    super(changeDetectorRef, formBuild);
  }

  initTypes(): void {
    this.bodaccPublicationTypeService.getBodaccPublicationTypes().subscribe(response => {
      this.types = response;
    })
  }
}
