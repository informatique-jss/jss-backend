/* tslint:disable:no-unused-variable */
import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { VisitPrepaTiersResponsibleInfoComponent } from './visit-prepa-tiers-responsible-info.component';

describe('ResponsableMainComponent', () => {
  let component: VisitPrepaTiersResponsibleInfoComponent;
  let fixture: ComponentFixture<VisitPrepaTiersResponsibleInfoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ VisitPrepaTiersResponsibleInfoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(VisitPrepaTiersResponsibleInfoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
