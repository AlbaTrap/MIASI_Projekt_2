import { useEffect, useState } from 'react';

import { eventsApi } from '../../api/eventsApi';
import { EventCard } from '../../components/events/EventCard';
import { AppButton } from '../../components/ui/AppButton';
import { AppTextInput } from '../../components/ui/AppTextInput';
import { EmptyState } from '../../components/ui/EmptyState';
import { LoadingState } from '../../components/ui/LoadingState';
import { mapFilterOptionsDto, mapSearchResultsListDto } from '../../mappers/eventMapper';
import { EventFilterOptions, SearchResultList, SearchSortDirection, SearchSortField } from '../../types/events';
import { isDateInputValid, toEndOfDayIso, toStartOfDayIso } from '../../utils/date';
import { CatalogFilterPanel } from './CatalogFilterPanel';

const PAGE_SIZE = 5;

const emptyResultList: SearchResultList = {
  results: [],
  resultCount: 0,
  pageNumber: 1,
  pageSize: PAGE_SIZE,
  totalPages: 1,
};

export function EventCatalogPage() {
  const [resultList, setResultList] = useState<SearchResultList>(emptyResultList);
  const [filterOptions, setFilterOptions] = useState<EventFilterOptions>({
    categories: [],
    locations: [],
  });
  const [phrase, setPhrase] = useState('');
  const [category, setCategory] = useState('');
  const [dateFrom, setDateFrom] = useState('');
  const [dateTo, setDateTo] = useState('');
  const [location, setLocation] = useState('');
  const [sortField, setSortField] = useState<SearchSortField>('DATE');
  const [sortDirection, setSortDirection] = useState<SearchSortDirection>('ASC');
  const [pageNumber, setPageNumber] = useState(1);
  const [isFilterPanelOpen, setIsFilterPanelOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  const hasDateError = !isDateInputValid(dateFrom) || !isDateInputValid(dateTo);

  useEffect(() => {
    let active = true;

    const loadFilters = async () => {
      try {
        const data = await eventsApi.getEventFilters();

        if (active) {
          setFilterOptions(mapFilterOptionsDto(data));
        }
      } catch (caughtError) {
        if (active) {
          setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się pobrać filtrów.');
        }
      }
    };

    void loadFilters();

    return () => {
      active = false;
    };
  }, []);

  useEffect(() => {
    let active = true;

    const loadEvents = async () => {
      if (hasDateError) {
        setResultList(emptyResultList);
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError('');
        const data = await eventsApi.searchEvents({
          phrase,
          category,
          location,
          from: toStartOfDayIso(dateFrom),
          to: toEndOfDayIso(dateTo),
          sortBy: sortField,
          direction: sortDirection,
          page: pageNumber - 1,
          size: PAGE_SIZE,
        });

        if (active) {
          setResultList(mapSearchResultsListDto(data));
        }
      } catch (caughtError) {
        if (active) {
          setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się pobrać wydarzeń.');
          setResultList(emptyResultList);
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
  }, [category, dateFrom, dateTo, hasDateError, location, pageNumber, phrase, sortDirection, sortField]);

  const resetPage = () => setPageNumber(1);

  const clearFilters = () => {
    setPhrase('');
    setCategory('');
    setDateFrom('');
    setDateTo('');
    setLocation('');
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

  const handleSortChange = (field: SearchSortField, direction: SearchSortDirection) => {
    setSortField(field);
    setSortDirection(direction);
    resetPage();
  };

  const activeFilterCount = [category, dateFrom, dateTo, location].filter(Boolean).length;

  const renderFilters = (idPrefix: string) => (
    <CatalogFilterPanel
      availableCategories={filterOptions.categories}
      availableLocations={filterOptions.locations}
      category={category}
      dateFrom={dateFrom}
      dateTo={dateTo}
      idPrefix={idPrefix}
      location={location}
      onCategoryChange={handleCategoryChange}
      onClearFilters={clearFilters}
      onDateFromChange={handleDateFromChange}
      onDateToChange={handleDateToChange}
      onLocationChange={handleLocationChange}
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
              disabled={resultList.pageNumber <= 1 || hasDateError || isLoading}
              onClick={() => setPageNumber((current) => Math.max(1, current - 1))}
              variant="secondary"
            >
              Poprzednia strona
            </AppButton>
            <AppButton
              disabled={resultList.pageNumber >= resultList.totalPages || hasDateError || isLoading}
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
