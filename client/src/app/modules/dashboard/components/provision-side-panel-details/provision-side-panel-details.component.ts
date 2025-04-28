import { Component, Input, OnInit } from '@angular/core';
import { Provision } from 'src/app/modules/quotation/model/Provision';

@Component({
  selector: 'provision-side-panel-details',
  templateUrl: './provision-side-panel-details.component.html',
  styleUrls: ['./provision-side-panel-details.component.css']
})
export class ProvisionSidePanelDetailsComponent implements OnInit {

  @Input() provision: Provision | undefined;
  @Input() displayStatus: boolean = true;

  constructor() { }

  ngOnInit() {
  }

}
