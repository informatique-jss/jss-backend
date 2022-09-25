/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { SelectOrderingStatusComponent } from './select-quotation-status.component';

describe('SelectOrderingStatusComponent', () => {
  let component: SelectOrderingStatusComponent;
  let fixture: ComponentFixture<SelectOrderingStatusComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [SelectOrderingStatusComponent]
    })
      .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(SelectOrderingStatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
