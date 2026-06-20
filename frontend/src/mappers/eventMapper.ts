import { EventDto } from '../api/backendTypes';
import { CatalogEvent, SearchResult } from '../types/events';
import { getEventLifecyclePresentation } from '../features/events/eventLifecycle';

const buildShortDescription = (description: string) => {
  const normalized = description.replace(/\s+/g, ' ').trim();
  return normalized.length > 132 ? `${normalized.slice(0, 129).trim()}...` : normalized;
};

export const mapEventDto = (dto: EventDto): CatalogEvent => ({
  id: dto.id,
  title: dto.title,
  description: dto.description || '',
  city: dto.city,
  address: dto.address || '',
  startDate: dto.startDate,
  endDate: dto.endDate,
  category: dto.category || 'inne',
  status: dto.status,
  updateNotice: dto.updateNotice,
});

export const mapEventToSearchResult = (event: CatalogEvent): SearchResult => {
  const lifecycle = getEventLifecyclePresentation(event.status, event.updateNotice);

  return {
    eventId: event.id,
    name: event.title,
    shortDescription: buildShortDescription(event.description),
    startsAt: event.startDate,
    location: [event.address, event.city].filter(Boolean).join(', '),
    category: event.category,
    status: event.status,
    lifecycleNotice: lifecycle.notice,
  };
};
