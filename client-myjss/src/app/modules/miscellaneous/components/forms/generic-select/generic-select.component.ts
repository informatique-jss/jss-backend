import { Directive, EventEmitter, OnInit, Output } from '@angular/core';
import { UntypedFormBuilder } from '@angular/forms';
import { AppService } from '../../../../../libs/app.service';
import { GenericFormComponent } from '../generic-form.components';

@Directive()
export abstract class GenericSelectComponent<T> extends GenericFormComponent implements OnInit {

  /** Événement déclenché lorsque la valeur change */
  @Output() selectionChange: EventEmitter<T> = new EventEmitter();

  abstract types: T[];

  constructor(
    private formBuilder3: UntypedFormBuilder, private appService2: AppService
  ) {
    super(formBuilder3,)
  }

  override ngOnInit(): void {
    this.initTypes();

    if (this.types) {
      this.types.sort((a, b) => this.displayLabel(a).localeCompare(this.displayLabel(b)));
    }

    if (this.form) {
      this.form.get(this.propertyName)?.valueChanges.subscribe((newValue) => {
        this.selectionChange.emit(newValue); // Émet la nouvelle valeur sélectionnée
      });
    }
  }

  /** Méthode abstraite à implémenter pour initialiser `types` */
  abstract initTypes(): void;

  /** Comparateur pour la sélection des valeurs */
  compareWithId(a: any, b: any): boolean {
    return a && b ? a.id === b.id : a === b;
  }

  /** Efface le champ et réinitialise les événements */
  clearField(): void {
    if (this.form)
      this.form.get(this.propertyName)?.setValue(null);
    this.selectionChange.emit(undefined);
  }

  /** Méthode pour récupérer un lien d’aperçu (optionnelle) */
  getPreviewActionLinkFunction(entity: T): string[] | undefined {
    return undefined;
  }

  /** Affichage des labels */
  override displayLabel(object: any): string {
    if (object && object.label) return object.label;
    if (object && object.name) return object.name;
    if (typeof object === 'string') return object;
    return '';
  }
}
