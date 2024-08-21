import { Component, OnInit } from "@angular/core";
import { FormBuilder } from "@angular/forms";
import { MatDialogRef } from "@angular/material/dialog";
import { FormaliteDialogChoose } from '../../model/FormaliteDialogChoose';
import { ConstantService } from "src/app/modules/miscellaneous/services/constant.service";

@Component({
  selector: 'formalite-associate-dialog',
  templateUrl: './formalite-associate-dialog.html',
  styleUrls: ['./formalite-associate-dialog.css']
})
export class FormaliteAssociateDialog implements OnInit {

  assoFormaliteTypeForm = this.formBuilder.group({});
  formaliteDialogChoose: FormaliteDialogChoose = {} as FormaliteDialogChoose;

  competentAuthorityInpi = this.constantService.getCompetentAuthorityInpi();
  competentAuthorityInfogreffe = this.constantService.getCompetentAuthorityInfogreffe();

  constructor(
    public dialogRef: MatDialogRef<FormaliteAssociateDialog>,
    private constantService: ConstantService,
    private formBuilder: FormBuilder,
  ) { }

  ngOnInit() { }

  onConfirm(): void {
    this.dialogRef.close(this.formaliteDialogChoose);
  }

  onClose(): void {
    this.dialogRef.close();
  }
}
