import { useEffect, useMemo, useState } from 'react';

import { eventsApi } from '../../api/eventsApi';
import { EventCard } from '../../components/events/EventCard';
import { AppButton } from '../../components/ui/AppButton';
import { AppTextInput } from '../../components/ui/AppTextInput';
import { EmptyState } from '../../components/ui/EmptyState';
import { LoadingState } from '../../components/ui/LoadingState';
import { mapEventDto } from '../../mappers/eventMapper';
import { CatalogEvent, SearchSortDirection, SearchSortField } from '../../types/events';
import { isDateInputValid } from '../../utils/date';
import { CatalogFilterPanel } from './CatalogFilterPanel';
import { isVisibleInCatalog } from './eventLifecycle';
import { searchEvents } from './searchEvents';

const PAGE_SIZE = 5;

export function EventCatalogPage() {
  const [events, setEvents] = useState<CatalogEvent[]>([]);
  const [phrase, setPhrase] = useState('');
  const [category, setCategory] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [location, setLocation] = useState('');
  const [selectedLocations, setSelectedLocations] = useState<string[]>([]);
  const [sortField, setSortField] = useState<SearchSortField>('DATE');
  const [sortDirection, setSortDirection] = useState<SearchSortDirection>('ASC');
  const [pageNumber, setPageNumber] = useState(1);
  const [isFilterPanelOpen, setIsFilterPanelOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    const loadEvents = async () => {
      try {
        setIsLoading(true);
        setError('');
        const data = await eventsApi.searchEvents();

        if (active) {
          setEvents(data.map(mapEventDto));
        }
      } catch (caughtError) {
        if (active) {
          setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się pobrać wydarzeń.');
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    };

    void loadEvents();

    return () => {
      active = false;
    };
  }, []);

  const availableEvents = useMemo(
    () => events.filter(isVisibleInCatalog),
    [events],
  );

  const availableCategories = useMemo(
    () =>
      Array.from(new Set(availableEvents.map((event) => event.category)))
        .filter(Boolean)
        .sort((left, right) => left.localeCompare(right, 'pl')),
    [availableEvents],
  );

  const availableLocations = useMemo(
    () =>
      Array.from(new Set(availableEvents.map((event) => event.address).filter(Boolean)))
        .sort((left, right) => left.localeCompare(right, 'pl')),
    [availableEvents],
  );

  const resultList = useMemo(
    () =>
      searchEvents(events, {
        phrase,
        category,
        dateFrom: isDateInputValid(dateFrom) ? dateFrom : '',
        dateTo: isDateInputValid(dateTo) ? dateTo : '',
        location,
        selectedLocations,
        sortField,
        sortDirection,
        pageNumber,
        pageSize: PAGE_SIZE,
      }),
    [category, dateFrom, dateTo, events, location, pageNumber, phrase, selectedLocations, sortDirection, sortField],
  );

  const resetPage = () => setPageNumber(1);

  const clearFilters = () => {
    setPhrase('');
    setCategory('');
    setDateFrom('');
    setDateTo('');
    setLocation('');
    setSelectedLocations([]);
    setSortField('DATE');
    setSortDirection('ASC');
    setPageNumber(1);
  };

  const handleCategoryChange = (value: string) => {
    setCategory(value);
    resetPage();
  };

  const handleDateFromChange = (value: string) => {
    setDateFrom(value);
    resetPage();
  };

  const handleDateToChange = (value: string) => {
    setDateTo(value);
    resetPage();
  };

  const handleLocationChange = (value: string) => {
    setLocation(value);
    resetPage();
  };

  const handleSelectedLocationsChange = (value: string[]) => {
    setSelectedLocations(value);
    resetPage();
  };

  const handleSortChange = (field: SearchSortField, direction: SearchSortDirection) => {
    setSortField(field);
    setSortDirection(direction);
    resetPage();
  };

  const hasDateError = !isDateInputValid(dateFrom) || !isDateInputValid(dateTo);
  const activeFilterCount =
    [category, dateFrom, dateTo, location].filter(Boolean).length + (selectedLocations.length > 0 ? 1 : 0);

  const renderFilters = (idPrefix: string) => (
    <CatalogFilterPanel
      availableCategories={availableCategories}
      availableLocations={availableLocations}
      category={category}
      dateFrom={dateFrom}
      dateTo={dateTo}
      idPrefix={idPrefix}
      location={location}
      selectedLocations={selectedLocations}
      onCategoryChange={handleCategoryChange}
      onClearFilters={clearFilters}
      onDateFromChange={handleDateFromChange}
      onDateToChange={handleDateToChange}
      onLocationChange={handleLocationChange}
      onSelectedLocationsChange={handleSelectedLocationsChange}
      onSortChange={handleSortChange}
      sortDirection={sortDirection}
      sortField={sortField}
    />
  );

  return (
    <main className="page-shell catalog-page">
      <section className="hero-panel catalog-hero">
        <div className="hero-copy">
          <p className="eyebrow">Wrocław na dziś i jutro</p>
          <h1>Znajdź wydarzenie, które pasuje do Twojego dnia</h1>
          <p>
            Przeglądaj aktualne wydarzenia we Wrocławiu po frazie, kategorii, terminie i miejscu.
          </p>
        </div>

        <div className="hero-search-card" aria-label="Główna wyszukiwarka wydarzeń">
          <AppTextInput
            className="search-field-large"
            label="Szukaj wydarzeń"
            onChange={(event) => {
              setPhrase(event.target.value);
              resetPage();
            }}
            placeholder="Koncert, warsztaty, miejsce lub kategoria"
            value={phrase}
          />
        </div>
      </section>

      <div className="mobile-filter-actions">
        <AppButton onClick={() => setIsFilterPanelOpen(true)} variant="secondary">
          Filtry {activeFilterCount > 0 ? `(${activeFilterCount})` : ''}
        </AppButton>
      </div>

      <div className={`filter-drawer ${isFilterPanelOpen ? 'filter-drawer-open' : ''}`}>
        <button
          aria-label="Zamknij filtry"
          className="filter-drawer-backdrop"
          onClick={() => setIsFilterPanelOpen(false)}
          type="button"
        />
        <div className="filter-drawer-panel">
          <div className="filter-drawer-header">
            <h2>Filtry</h2>
            <button className="filter-close-button" onClick={() => setIsFilterPanelOpen(false)} type="button">
              Zamknij
            </button>
          </div>
          {renderFilters('mobile-filters')}
        </div>
      </div>

      <div className="catalog-layout">
        <aside className="catalog-sidebar">{renderFilters('desktop-filters')}</aside>

        <section className="catalog-results" aria-label="Wyniki wyszukiwania wydarzeń">
          <div className="panel result-summary">
            <div>
              <p className="eyebrow">Wyniki</p>
              <h2>Znaleziono {resultList.resultCount}</h2>
            </div>
            <p>
              Strona {resultList.pageNumber} z {resultList.totalPages}
            </p>
          </div>

          {isLoading ? <LoadingState /> : null}
          {error ? <div className="notice notice-error">{error}</div> : null}

          {!isLoading && !error && resultList.results.length === 0 ? (
            <EmptyState title="Brak wyników">
              Brak wydarzeń pasujących do wybranych kryteriów.
            </EmptyState>
          ) : null}

          {!isLoading && !error && resultList.results.length > 0 ? (
            <section className="event-list" aria-label="Lista wydarzeń">
              {resultList.results.map((event) => (
                <EventCard event={event} key={event.eventId} />
              ))}
            </section>
          ) : null}

          <div className="pagination">
            <AppButton
              disabled={resultList.pageNumber <= 1 || hasDateError}
              onClick={() => setPageNumber((current) => Math.max(1, current - 1))}
              variant="secondary"
            >
              Poprzednia strona
            </AppButton>
            <AppButton
              disabled={resultList.pageNumber >= resultList.totalPages || hasDateError}
              onClick={() => setPageNumber((current) => current + 1)}
              variant="secondary"
            >
              Następna strona
            </AppButton>
          </div>
        </section>
      </div>
    </main>
  );
}
