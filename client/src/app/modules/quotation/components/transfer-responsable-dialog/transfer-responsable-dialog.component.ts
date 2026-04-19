import { Component } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { ResponsableService } from 'src/app/modules/tiers/services/responsable.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppService } from 'src/app/services/app.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
    selector: 'transfer-responsable-dialog',
    templateUrl: './transfer-responsable-dialog.component.html',
    styleUrls: ['./transfer-responsable-dialog.component.css'],
    standalone: false
})
export class TransferResponsableDialogComponent {

  selectedResponsable: Responsable = {} as Responsable;
  newResponsable: Responsable | undefined;

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>,
    private responsableService: ResponsableService,
    private appService: AppService,
  ) { }
  transferResponsableForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.transferResponsableForm.valid;
  }

  transferResponsable() {
    if (!this.selectedResponsable || !this.newResponsable) {
      this.appService.displaySnackBar("Veuillez renseigner le nouveau responsable", true, 30);
      return;
    }
    if (this.newResponsable?.id == this.selectedResponsable.id) {
      this.appService.displaySnackBar("Veuillez choisir un responsable différent du responsable actuel", true, 30);
    } else {
      this.responsableService.transferResponsable(this.selectedResponsable.id, this.newResponsable.id).subscribe(response => {
        this.appService.openRoute(null, '/tiers/responsable/' + response.id, null);
      });
    }
    this.dialogRef.close(this.selectedResponsable);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

  fillResponsable(responsable: IndexEntity) {
    this.responsableService.getResponsable(responsable.entityId).subscribe(response => {
      this.newResponsable = response;
    });
  }
}
