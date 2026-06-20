import { CatalogEvent, EventStatus } from '../../types/events';

type EventLifecyclePresentation = {
  label: string;
  className: string;
  visualTone: 'normal' | 'cancelled' | 'archived';
  notice?: string;
};

const catalogVisibleStatuses = new Set<EventStatus>(['AVAILABLE', 'PUBLISHED', 'UPDATED']);
const savedVisibleStatuses = new Set<EventStatus>([
  'AVAILABLE',
  'PUBLISHED',
  'UPDATED',
  'CANCELLED',
  'ARCHIVED',
]);

export const isVisibleInCatalog = (event: CatalogEvent) => catalogVisibleStatuses.has(event.status);

export const isVisibleInSavedEvents = (event: CatalogEvent) => savedVisibleStatuses.has(event.status);

export const getEventLifecyclePresentation = (
  status: EventStatus,
  updateNotice?: string,
): EventLifecyclePresentation => {
  if (status === 'CANCELLED') {
    return {
      label: 'ANULOWANE',
      className: 'event-lifecycle-cancelled',
      visualTone: 'cancelled',
      notice: 'To wydarzenie zostało anulowane.',
    };
  }

  if (status === 'ARCHIVED') {
    return {
      label: 'ZARCHIWIZOWANE',
      className: 'event-lifecycle-archived',
      visualTone: 'archived',
      notice: 'To wydarzenie jest archiwalne.',
    };
  }

  if (status === 'UPDATED') {
    return {
      label: 'ZAKTUALIZOWANE',
      className: 'event-lifecycle-updated',
      visualTone: 'normal',
      notice: updateNotice || 'Opis wydarzenia został zaktualizowany.',
    };
  }

  return {
    label: 'OPUBLIKOWANE',
    className: 'event-lifecycle-published',
    visualTone: 'normal',
  };
};
