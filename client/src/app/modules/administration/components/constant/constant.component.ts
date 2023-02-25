import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subscription } from 'rxjs';
import { Constant } from 'src/app/modules/miscellaneous/model/Constant';
import { ConstantService } from 'src/app/modules/miscellaneous/services/constant.service';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'constant',
  templateUrl: './constant.component.html',
  styleUrls: ['./constant.component.css']
})
export class ConstantComponent implements OnInit {

  constructor(private constantService: ConstantService,
    private appService: AppService,
    private formBuilder: FormBuilder) { }

  constant: Constant = {} as Constant;

  constantForm = this.formBuilder.group({});
  editMode: boolean = false;

  saveObservableSubscription: Subscription = new Subscription;

  ngOnInit() {
    this.refreshConstant();

    this.saveObservableSubscription = this.appService.saveObservable.subscribe(response => {
      if (response)
        if (this.editMode)
          this.saveConstant()
        else
          this.editConstant()
    });
  }

  ngOnDestroy() {
    this.saveObservableSubscription.unsubscribe();
  }

  refreshConstant() {
    this.constantService.getConstants().subscribe(response => {
      if (response && response)
        this.constant = response;
    })
  }

  editConstant() {
    this.editMode = true;
  }

  saveConstant() {
    if (this.constantForm.valid) {
      this.constantService.addOrUpdateConstant(this.constant).subscribe(response => {
        this.refreshConstant();
        this.editMode = false;
      });
    } else {
      this.appService.displaySnackBar("Veuiller remplir l'ensemble des champs", true, 20);
    }
  }
}
