import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { Responsable } from 'src/app/modules/tiers/model/Responsable';
import { ResponsableService } from 'src/app/modules/tiers/services/responsable.service';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppService } from 'src/app/services/app.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'transfer-responsible-dialog',
  templateUrl: './transfer-responsible-dialog.component.html',
  styleUrls: ['./transfer-responsible-dialog.component.css']
})
export class TransferResponsibleDialogComponent implements OnInit {

  selectedResponsable: Responsable = {} as Responsable;
  newResponsable: Responsable | undefined;

  constructor(private formBuilder: FormBuilder,
    private dialogRef: MatDialogRef<AddAffaireDialogComponent>,
    private responsableService: ResponsableService,
    private appService: AppService,
  ) { }

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }


  transferResponsibleForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.transferResponsibleForm.valid;
  }

  transferResponsible() {

    if (!this.selectedResponsable || !this.newResponsable) {
      this.appService.displaySnackBar("Veuillez renseigner un nouveau responsable", true, 30);
      return;
    }
    if (this.newResponsable?.id == this.selectedResponsable.id) {
      this.appService.displaySnackBar("Veuillez choisir un responsable différent de l'actuel", true, 30);
    } else {
      this.responsableService.transferResponsable(this.selectedResponsable.id, this.newResponsable.id)
      this.dialogRef.close(this.selectedResponsable);
    }

  }

  closeDialog() {
    this.dialogRef.close(null);
  }

  fillResponsable(responsable: IndexEntity) {
    this.responsableService.getResponsable(responsable.entityId).subscribe(response => {
      this.newResponsable = response;
    })
  }
}
