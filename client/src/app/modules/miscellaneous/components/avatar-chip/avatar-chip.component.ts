import { Component, Input, OnInit } from '@angular/core';
import { displayInTeams } from 'src/app/libs/MailHelper';
import { Employee } from 'src/app/modules/profile/model/Employee';

@Component({
  selector: 'avatar-chip',
  templateUrl: './avatar-chip.component.html',
  styleUrls: ['./avatar-chip.component.css']
})
export class AvatarChipComponent implements OnInit {

  @Input() employee: Employee | undefined;

  constructor() { }

  ngOnInit() {
  }

  displayInTeams = displayInTeams;

}
