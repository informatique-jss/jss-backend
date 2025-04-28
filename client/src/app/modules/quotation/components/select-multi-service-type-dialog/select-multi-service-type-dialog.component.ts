import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { Affaire } from '../../model/Affaire';
import { ServiceType } from '../../model/ServiceType';
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
  customLabel: string | undefined;
  isJustSelectServiceType: boolean = false;

  constructor(private formBuilder: FormBuilder,
    private dialog: MatDialog,
    private serviceService: ServiceService,
    public dialogRef: MatDialogRef<SelectMultiServiceTypeDialogComponent>
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
      this.serviceService.getServiceForMultiServiceTypesAndAffaire(this.selectedServiceTypes, this.affaire, this.customLabel).subscribe(response => {
        if (response) {
          // response[0].customerComment = this.customerComment;
          this.dialogRef.close(response);
        }
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
