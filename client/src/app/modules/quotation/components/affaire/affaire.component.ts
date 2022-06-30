import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { MatDialog } from '@angular/material/dialog';
import { CustomErrorStateMatcher } from 'src/app/app.component';
import { UNREGISTERED_COMPANY_LEGAL_FORM_CODE } from 'src/app/libs/Constants';
import { IndexEntity } from 'src/app/routing/search/IndexEntity';
import { Affaire } from '../../model/Affaire';
import { AffaireService } from '../../services/affaire.service';
import { AddAffaireDialogComponent } from '../add-affaire-dialog/add-affaire-dialog.component';

@Component({
  selector: 'affaire',
  templateUrl: './affaire.component.html',
  styleUrls: ['./affaire.component.css']
})
export class AffaireComponent implements OnInit {
  matcher: CustomErrorStateMatcher = new CustomErrorStateMatcher();
  @Input() affaire: Affaire = {} as Affaire;
  @Output() affaireChange: EventEmitter<any> = new EventEmitter<any>();
  @Input() editMode: boolean = false;

  UNREGISTERED_COMPANY_LEGAL_FORM_CODE = UNREGISTERED_COMPANY_LEGAL_FORM_CODE;

  constructor(private formBuilder: UntypedFormBuilder,
    public addAffaireDialog: MatDialog,
    private affaireService: AffaireService,
  ) { }

  ngOnInit() {
  }

  affaireForm = this.formBuilder.group({
  });

  openAddOfferDialog() {
    let addAffaireDialog = this.addAffaireDialog.open(AddAffaireDialogComponent, {
      width: '90%'
    });
    addAffaireDialog.afterClosed().subscribe(response => {
      if (response && response != null) {
        this.affaire = response;
        this.affaireChange.emit(this.affaire);
      }
    });
  }

  selectedAffaire: IndexEntity = {} as IndexEntity;
  fillAffaire(entity: IndexEntity) {
    this.affaireService.getAffaire(entity.entityId).subscribe(affaire => {
      this.affaire = affaire;
      this.affaireChange.emit(this.affaire);
    });
  }
}
