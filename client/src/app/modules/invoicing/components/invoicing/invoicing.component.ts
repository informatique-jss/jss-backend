import { Component, OnInit } from '@angular/core';
import { AppService } from 'src/app/services/app.service';

@Component({
  selector: 'invoicing',
  templateUrl: './invoicing.component.html',
  styleUrls: ['./invoicing.component.css']
})
export class InvoiceComponent implements OnInit {

  constructor(
    private appService: AppService,
  ) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Facturation");
  }

}
