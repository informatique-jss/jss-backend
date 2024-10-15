import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AppService } from '../../../../libs/app.service';

@Component({
  selector: 'app-edit-address',
  templateUrl: './edit-address.component.html',
  styleUrls: ['./edit-address.component.css']
})
export class EditAddressComponent implements OnInit {

  idOrder: number | undefined;

  constructor(private activatedRoute: ActivatedRoute,
    private formBuilder: FormBuilder,
    private appService: AppService,) { }

  ngOnInit() {
    this.idOrder = this.activatedRoute.snapshot.params['idOrder'];
  }

}
