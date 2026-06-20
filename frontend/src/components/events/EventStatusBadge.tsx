import { EventStatus } from '../../types/events';

type EventStatusBadgeProps = {
  status: EventStatus;
};

const labels: Record<EventStatus, string> = {
  AVAILABLE: 'Opublikowane',
  PUBLISHED: 'Opublikowane',
  UPDATED: 'Zaktualizowane',
  HIDDEN: 'Ukryte',
  CANCELLED: 'Anulowane',
  ARCHIVED: 'Zarchiwizowane',
  NORMALIZED: 'Znormalizowane',
  RAW: 'Surowe',
  REJECTED: 'Odrzucone',
};

export function EventStatusBadge({ status }: EventStatusBadgeProps) {
  return <span className={`badge event-status-${status}`}>{labels[status]}</span>;
}
