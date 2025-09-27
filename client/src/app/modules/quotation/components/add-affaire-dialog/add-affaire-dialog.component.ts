import { Component, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AddAffaireComponent } from 'src/app/modules/quotation/components/add-affaire/add-affaire.component';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { AppService } from 'src/app/services/app.service';
import { Affaire } from '../../model/Affaire';
import { AffaireService } from '../../services/affaire.service';

@Component({
  selector: 'app-add-affaire-dialog',
  templateUrl: './add-affaire-dialog.component.html',
  styleUrls: ['./add-affaire-dialog.component.css']
})
export class AddAffaireDialogComponent implements OnInit {

  @ViewChild(AddAffaireComponent) addAffaireComponent: AddAffaireComponent | undefined;

  affaire: Affaire = {} as Affaire;
  selectedAffaire: Affaire | null = null;
  isLabelAffaire: boolean = false;


  constructor(private formBuilder: FormBuilder,
    private affaireService: AffaireService,
    private appService: AppService,
    private affaireDialogRef: MatDialogRef<AddAffaireDialogComponent>
  ) { }

  ngOnInit() {
    if (this.affaire && !this.affaire.isIndividual)
      this.affaire.isIndividual = false;
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.affaire != undefined) {
      if (this.affaire && !this.affaire.isIndividual)
        this.affaire.isIndividual = false;
      this.affaireForm.markAllAsTouched();
    }
  }

  fillAffaire(entity: IndexEntity) {
    this.affaireService.getAffaire(entity.entityId).subscribe(affaire => {
      this.selectedAffaire = affaire;
    });
  }

  affaireForm = this.formBuilder.group({
  });

  getFormStatus(): boolean {
    return this.affaireForm.valid && this.addAffaireComponent!.getFormStatus();
  }

  saveAffaire() {
    if (this.selectedAffaire) {
      this.affaireService.addOrUpdateAffaire(this.addAffaireComponent!.affaire).subscribe(response => {
        this.affaire = response;
        this.affaireDialogRef.close(this.selectedAffaire);
      })
    } else if (this.getFormStatus()) {
      this.affaireService.addOrUpdateAffaire(this.addAffaireComponent!.affaire).subscribe(response => {
        this.affaire = response;
        this.affaireDialogRef.close(this.affaire);
      })
    } else {
      this.appService.displaySnackBar("L'onglet de saisie de l'affaire n'est pas correctement rempli. Veuillez le compléter avant de sauvegarder", true, 20);
    }
  }

  closeDialog() {
    this.affaireDialogRef.close(null);
  }
}
