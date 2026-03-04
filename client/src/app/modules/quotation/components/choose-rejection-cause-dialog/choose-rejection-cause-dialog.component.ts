import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { FormaliteGuichetUniqueService } from 'src/app/modules/miscellaneous/services/formalite.guichet.unique.service';
import { FormaliteGuichetUnique } from '../../model/guichet-unique/FormaliteGuichetUnique';
import { RegularizationRequest } from '../../model/guichet-unique/RegularizationRequest';
import { RejectionCause } from '../../model/RejectionCause';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'choose-rejection-cause-dialog',
  templateUrl: './choose-rejection-cause-dialog.component.html',
  styleUrls: ['./choose-rejection-cause-dialog.component.css']
})
export class ChooseRejectionCauseDialogComponent implements OnInit {

  regularizationRequest: RegularizationRequest | undefined;
  formaliteGuichetUnique: FormaliteGuichetUnique | undefined;
  label: string = "";
  title: string = "";
  rejectionCause: RejectionCause | undefined;

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>,
    private formaliteGuichetUniqueService: FormaliteGuichetUniqueService
  ) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  regularizationRequestForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.regularizationRequestForm.valid;
  }

  chooseRejectionCause() {
    if (this.rejectionCause && this.regularizationRequest && this.formaliteGuichetUnique) {
      this.formaliteGuichetUniqueService.changeRejectionCause(this.formaliteGuichetUnique, this.regularizationRequest, this.rejectionCause).subscribe(reponse => {
        this.dialogRef.close(this.rejectionCause);
      })
    }
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
