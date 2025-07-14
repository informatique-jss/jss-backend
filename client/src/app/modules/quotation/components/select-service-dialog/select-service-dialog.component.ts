import { Component, OnInit, SimpleChanges } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { AssoAffaireOrder } from '../../model/AssoAffaireOrder';
import { Service } from '../../model/Service';

@Component({
  selector: 'app-select-service-dialog',
  templateUrl: './select-service-dialog.component.html',
  styleUrls: ['./select-service-dialog.component.css']
})
export class SelectServiceDialogComponent implements OnInit {

  assoAffaireOrder: AssoAffaireOrder | undefined;
  selectedService: Service | undefined;
  currentService: Service | undefined;

  constructor(private formBuilder: FormBuilder,
    public dialogRef: MatDialogRef<SelectServiceDialogComponent>
  ) { }

  serviceTypeForm = this.formBuilder.group({});

  ngOnInit() {
  }

  ngOnChanges(changes: SimpleChanges) {
  }

  getFormStatus(): boolean {
    return this.serviceTypeForm.valid;
  }

  selectService(service: Service) {
    this.selectedService = service;
  }

  validateService() {
    if (this.selectedService)
      this.dialogRef.close(this.selectedService);
  }

  closeDialog() {
    this.dialogRef.close(null);
  }

}
