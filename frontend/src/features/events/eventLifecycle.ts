import { CatalogEvent, EventStatus } from '../../types/events';

type EventLifecyclePresentation = {
  label: string;
  className: string;
  visualTone: 'normal' | 'cancelled' | 'archived';
  notice?: string;
};

export const isVisibleInCatalog = (event: CatalogEvent) => event.status === 'PUBLISHED';

export const isVisibleInSavedEvents = (event: CatalogEvent) =>
  event.status === 'PUBLISHED' || event.status === 'CANCELLED' || event.status === 'ARCHIVED';

export const isUnavailableForUser = (status: EventStatus) => status === 'DRAFT' || status === 'HIDDEN';

export const getEventLifecyclePresentation = (status?: EventStatus): EventLifecyclePresentation => {
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

  if (status === 'DRAFT') {
    return {
      label: 'SZKIC',
      className: 'event-lifecycle-hidden',
      visualTone: 'normal',
      notice: 'To wydarzenie nie jest jeszcze opublikowane.',
    };
  }

  if (status === 'HIDDEN') {
    return {
      label: 'UKRYTE',
      className: 'event-lifecycle-hidden',
      visualTone: 'normal',
      notice: 'To wydarzenie nie jest dostępne.',
    };
  }

  return {
    label: 'OPUBLIKOWANE',
    className: 'event-lifecycle-published',
    visualTone: 'normal',
  };
};
