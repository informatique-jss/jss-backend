import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Affaire } from '../../model/Affaire';
import { ServiceType } from '../../model/ServiceType';
import { ServiceService } from '../../services/service.service';

@Component({
  selector: 'app-select-service-type-dialog',
  templateUrl: './select-service-type-dialog.component.html',
  styleUrls: ['./select-service-type-dialog.component.css']
})
export class SelectServiceTypeDialogComponent implements OnInit {

  affaire: Affaire | undefined;
  selectedServiceType: ServiceType | undefined;
  customerComment: string | undefined;
  isJustSelectServiceType: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private serviceService: ServiceService,
    public dialogRef: MatDialogRef<SelectServiceTypeDialogComponent>
  ) { }

  serviceTypeForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  getFormStatus(): boolean {
    return this.serviceTypeForm.valid;
  }

  generateService() {
    if (this.selectedServiceType && this.affaire)
      this.serviceService.getServiceForServiceTypeAndAffaire(this.selectedServiceType, this.affaire).subscribe(response => {
        if (this.customerComment)
          response.customerComment = this.customerComment;
        this.dialogRef.close(response);
      })
  }

  chooseService() {
    if (this.selectedServiceType)
      this.dialogRef.close(this.selectedServiceType);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

}
