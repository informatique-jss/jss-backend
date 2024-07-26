import { Component, OnInit } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { EventsService } from 'src/app/modules/miscellaneous/services/guichet-unique/events.service';
import { Events } from '../../../../../quotation/model/guichet-unique/referentials/Events';
import { GenericLocalAutocompleteComponent } from '../../generic-local-autocomplete/generic-local-autocomplete.component';

@Component({
  selector: 'autocomplete-events',
  templateUrl: '../../generic-local-autocomplete/generic-local-autocomplete.component.html',
  styleUrls: ['../../generic-local-autocomplete/generic-local-autocomplete.component.css']
})
export class AutocompleteEventsComponent extends GenericLocalAutocompleteComponent<Events> implements OnInit {

  types: Events[] = [] as Array<Events>;

  constructor(private formBuild: UntypedFormBuilder, private EventsService: EventsService,) {
    super(formBuild,)
  }

  filterEntities(types: Events[], value: string): Events[] {
    const filterValue = (value != undefined && value != null && value.toLowerCase != undefined) ? value.toLowerCase() : "";
    return types.filter(item =>
      item && item.label && item.code
      && (item.label.toLowerCase().includes(filterValue) || item.code.includes(filterValue)));
  }

  initTypes(): void {
    this.EventsService.getEvents().subscribe(response => this.types = response);
  }

}
