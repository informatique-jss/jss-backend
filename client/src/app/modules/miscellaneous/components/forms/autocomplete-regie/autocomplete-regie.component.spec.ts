/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AutocompleteRegieComponent } from './autocomplete-regie.component';

describe('AutocompleteBillingCenterComponent', () => {
  let component: AutocompleteRegieComponent;
  let fixture: ComponentFixture<AutocompleteRegieComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AutocompleteRegieComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AutocompleteRegieComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
