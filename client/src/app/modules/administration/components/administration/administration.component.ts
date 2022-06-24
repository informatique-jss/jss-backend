import { Component, OnInit } from '@angular/core';
import { Subject } from 'rxjs';
import { AppService } from 'src/app/app.service';

@Component({
  selector: 'app-administration',
  templateUrl: './administration.component.html',
  styleUrls: ['./administration.component.css']
})
export class AdministrationComponent implements OnInit {

  selectedReferential: string = "";
  referentials: string[] = [] as Array<string>;
  selectedEntity: any | undefined;

  editMode: boolean = false;

  saveEvent: Subject<void> = new Subject<void>();
  addEvent: Subject<void> = new Subject<void>();

  ACT_TYPE_REFERENTIAL = "Type d'actes";

  constructor(private appService: AppService,) { }

  ngOnInit() {
    this.appService.changeHeaderTitle("Administration");
    this.referentials.push(this.ACT_TYPE_REFERENTIAL);
  }

  saveEntity() {
    this.saveEvent.next();
    this.editMode = false;
  }

  createEntity() {
    this.addEvent.next();
    this.editMode = true;
  }

  editEntity() {
    this.editMode = true;
  }

  changeSelectedEntity(element: any) {
    this.selectedEntity = element;
  }

  changeReferential() {
    this.selectedEntity = undefined;
    this.editMode = false;
  }
}
