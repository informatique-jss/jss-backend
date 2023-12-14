import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Observable } from 'rxjs';
import { TypeDocumentService } from 'src/app/modules/miscellaneous/services/guichet-unique/type.document.service';
import { TypeDocument } from 'src/app/modules/quotation/model/guichet-unique/referentials/TypeDocument';
import { AppService } from 'src/app/services/app.service';
import { GenericReferentialComponent } from '../generic-referential/generic-referential-component';

@Component({
  selector: 'referential-type-document',
  templateUrl: './referential-type-document.component.html',
  styleUrls: ['./../generic-referential/generic-referential.component.css']
})
export class ReferentialTypeDocumentComponent extends GenericReferentialComponent<TypeDocument> implements OnInit {
  constructor(private typeDocumentService: TypeDocumentService,
    private formBuilder2: FormBuilder,
    private appService2: AppService,) {
    super(formBuilder2, appService2);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (this.selectedEntity) {
      if (!this.selectedEntity.isToDownloadOnProvision)
        this.selectedEntity.isToDownloadOnProvision = false;
    }
  }

  getAddOrUpdateObservable(): Observable<TypeDocument> {
    return this.typeDocumentService.addOrUpdateTypeDocument(this.selectedEntity!);
  }
  getGetObservable(): Observable<TypeDocument[]> {
    return this.typeDocumentService.getTypeDocument();
  }
}
