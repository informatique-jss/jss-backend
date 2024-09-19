import { Component, OnInit, SimpleChanges } from '@angular/core';
import { ServiceType } from '../../model/ServiceType';
import { Affaire } from '../../model/Affaire';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { SelectServiceTypeDialogComponent } from '../select-service-type-dialog/select-service-type-dialog.component';
import { ServiceService } from '../../services/service.service';

@Component({
  selector: 'select-multi-service-type-dialog',
  templateUrl: './select-multi-service-type-dialog.component.html',
  styleUrls: ['./select-multi-service-type-dialog.component.css']
})
export class SelectMultiServiceTypeDialogComponent implements OnInit {

  affaire: Affaire | undefined;
  selectedServiceTypes: ServiceType[] | undefined;
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
    if (this.selectedServiceTypes && this.affaire)
      this.serviceService.getServiceForMultiServiceTypesAndAffaire(this.selectedServiceTypes, this.affaire).subscribe(response => {
        if (this.customerComment)
          response.customerComment = this.customerComment;
        this.dialogRef.close(response);
      })
  }

  chooseService() {
    if (this.selectedServiceTypes)
      this.dialogRef.close(this.selectedServiceTypes);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }
}
