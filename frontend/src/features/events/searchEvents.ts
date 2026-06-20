import { CatalogEvent, SearchQuery, SearchResultList, SearchSortField } from '../../types/events';
import { mapEventToSearchResult } from '../../mappers/eventMapper';
import { normalizeText } from '../../utils/text';
import { isVisibleInCatalog } from './eventLifecycle';

const compareByField = (left: CatalogEvent, right: CatalogEvent, field: SearchSortField) => {
  if (field === 'DATE') {
    return new Date(left.startDate).getTime() - new Date(right.startDate).getTime();
  }

  if (field === 'NAME') {
    return left.title.localeCompare(right.title, 'pl');
  }

  return left.category.localeCompare(right.category, 'pl');
};

const matchesDateRange = (event: CatalogEvent, query: SearchQuery) => {
  const startsAt = new Date(event.startDate).getTime();
  const from = query.dateFrom ? new Date(`${query.dateFrom}T00:00:00.000Z`).getTime() : undefined;
  const to = query.dateTo ? new Date(`${query.dateTo}T23:59:59.999Z`).getTime() : undefined;

  return (from === undefined || startsAt >= from) && (to === undefined || startsAt <= to);
};

export const searchEvents = (events: CatalogEvent[], query: SearchQuery): SearchResultList => {
  const phrase = normalizeText(query.phrase);
  const category = normalizeText(query.category);
  const location = normalizeText(query.location);
  const selectedLocations = query.selectedLocations.map(normalizeText);

  const matched = events
    .filter(isVisibleInCatalog)
    .filter((event) => !category || normalizeText(event.category) === category)
    .filter((event) => matchesDateRange(event, query))
    .filter((event) => {
      if (!phrase) {
        return true;
      }

      return normalizeText(
        [event.title, event.description, event.category, event.city, event.address].join(' '),
      ).includes(phrase);
    })
    .filter((event) => {
      if (!location) {
        return true;
      }

      return normalizeText([event.address, event.city].join(' ')).includes(location);
    })
    .filter((event) => {
      if (selectedLocations.length === 0) {
        return true;
      }

      return selectedLocations.includes(normalizeText(event.address));
    })
    .sort((left, right) => {
      const direction = query.sortDirection === 'ASC' ? 1 : -1;
      return compareByField(left, right, query.sortField) * direction;
    });

  const resultCount = matched.length;
  const pageSize = Math.max(1, query.pageSize);
  const totalPages = Math.max(1, Math.ceil(resultCount / pageSize));
  const pageNumber = Math.min(Math.max(1, query.pageNumber), totalPages);
  const start = (pageNumber - 1) * pageSize;

  return {
    results: matched.slice(start, start + pageSize).map(mapEventToSearchResult),
    resultCount,
    pageNumber,
    pageSize,
    totalPages,
  };
};
