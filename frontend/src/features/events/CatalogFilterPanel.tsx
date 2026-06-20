import { AppButton } from '../../components/ui/AppButton';
import { AppTextInput } from '../../components/ui/AppTextInput';
import { SearchSortDirection, SearchSortField } from '../../types/events';
import { isDateInputValid } from '../../utils/date';

type SortOption = {
  label: string;
  field: SearchSortField;
  direction: SearchSortDirection;
};

const sortOptions: SortOption[] = [
  { label: 'Najbliższe wydarzenia', field: 'DATE', direction: 'ASC' },
  { label: 'Najpóźniejsze wydarzenia', field: 'DATE', direction: 'DESC' },
  { label: 'Nazwa A-Z', field: 'NAME', direction: 'ASC' },
  { label: 'Nazwa Z-A', field: 'NAME', direction: 'DESC' },
  { label: 'Kategoria A-Z', field: 'CATEGORY', direction: 'ASC' },
  { label: 'Kategoria Z-A', field: 'CATEGORY', direction: 'DESC' },
];

const getSortOptionValue = (option: SortOption) => `${option.field}:${option.direction}`;

type CatalogFilterPanelProps = {
  availableCategories: string[];
  availableLocations: string[];
  category: string;
  dateFrom: string;
  dateTo: string;
  idPrefix: string;
  location: string;
  selectedLocations: string[];
  sortField: SearchSortField;
  sortDirection: SearchSortDirection;
  onCategoryChange: (value: string) => void;
  onDateFromChange: (value: string) => void;
  onDateToChange: (value: string) => void;
  onLocationChange: (value: string) => void;
  onSelectedLocationsChange: (value: string[]) => void;
  onSortChange: (field: SearchSortField, direction: SearchSortDirection) => void;
  onClearFilters: () => void;
};

export function CatalogFilterPanel({
  availableCategories,
  availableLocations,
  category,
  dateFrom,
  dateTo,
  idPrefix,
  location,
  selectedLocations,
  sortField,
  sortDirection,
  onCategoryChange,
  onDateFromChange,
  onDateToChange,
  onLocationChange,
  onSelectedLocationsChange,
  onSortChange,
  onClearFilters,
}: CatalogFilterPanelProps) {
  const currentSortValue = `${sortField}:${sortDirection}`;
  const selectedLocationSummary =
    selectedLocations.length === 0
      ? 'Wybierz lokalizacje'
      : `${selectedLocations.length} wybrano`;

  const handleSortValueChange = (value: string) => {
    const option = sortOptions.find((item) => getSortOptionValue(item) === value);

    if (option) {
      onSortChange(option.field, option.direction);
    }
  };

  const handleLocationToggle = (value: string) => {
    if (selectedLocations.includes(value)) {
      onSelectedLocationsChange(selectedLocations.filter((item) => item !== value));
      return;
    }

    onSelectedLocationsChange([...selectedLocations, value]);
  };

  return (
    <section className="filter-panel" aria-label="Filtry wydarzeń">
      <div className="filter-panel-heading">
        <p className="eyebrow">Dopasuj wyniki</p>
        <h2>Filtry</h2>
      </div>

      <div className="filter-group">
        <h3>Kategoria</h3>
        <div className="chip-row">
          <FilterChip label="Wszystkie" selected={!category} onClick={() => onCategoryChange('')} />
          {availableCategories.map((item) => (
            <FilterChip
              key={item}
              label={item}
              selected={category === item}
              onClick={() => onCategoryChange(item)}
            />
          ))}
        </div>
      </div>

      <div className="responsive-grid">
        <AppTextInput
          error={!isDateInputValid(dateFrom) ? 'Użyj formatu RRRR-MM-DD.' : undefined}
          id={`${idPrefix}-date-from`}
          label="Data od"
          onChange={(event) => onDateFromChange(event.target.value)}
          placeholder="RRRR-MM-DD"
          value={dateFrom}
        />
        <AppTextInput
          error={!isDateInputValid(dateTo) ? 'Użyj formatu RRRR-MM-DD.' : undefined}
          id={`${idPrefix}-date-to`}
          label="Data do"
          onChange={(event) => onDateToChange(event.target.value)}
          placeholder="RRRR-MM-DD"
          value={dateTo}
        />
      </div>

      <AppTextInput
        id={`${idPrefix}-location`}
        label="Lokalizacja"
        onChange={(event) => onLocationChange(event.target.value)}
        placeholder="Miejsce lub ulica"
        value={location}
      />

      <div className="filter-group">
        <h3>Dostępne lokalizacje</h3>
        <details className="dropdown-panel">
          <summary>{selectedLocationSummary}</summary>
          <div className="dropdown-content">
            {availableLocations.length > 0 ? (
              availableLocations.map((item) => (
                <label className="checkbox-option" key={item}>
                  <input
                    checked={selectedLocations.includes(item)}
                    onChange={() => handleLocationToggle(item)}
                    type="checkbox"
                  />
                  <span>{item}</span>
                </label>
              ))
            ) : (
              <p className="hint">Lokalizacje pojawią się po pobraniu wydarzeń.</p>
            )}
          </div>
        </details>
      </div>

      <label className="select-field" htmlFor={`${idPrefix}-sort`}>
        <span>Sortuj</span>
        <select
          id={`${idPrefix}-sort`}
          onChange={(event) => handleSortValueChange(event.target.value)}
          value={currentSortValue}
        >
          {sortOptions.map((option) => (
            <option key={getSortOptionValue(option)} value={getSortOptionValue(option)}>
              {option.label}
            </option>
          ))}
        </select>
      </label>

      <AppButton onClick={onClearFilters} variant="secondary">
        Wyczyść filtry
      </AppButton>
    </section>
  );
}

type FilterChipProps = {
  label: string;
  selected: boolean;
  onClick: () => void;
};

function FilterChip({ label, selected, onClick }: FilterChipProps) {
  return (
    <button className={`chip ${selected ? 'chip-selected' : ''}`} onClick={onClick} type="button">
      {label}
    </button>
  );
}
