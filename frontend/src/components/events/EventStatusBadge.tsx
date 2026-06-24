import { EventStatus } from '../../types/events';

type EventStatusBadgeProps = {
  status: EventStatus;
};

const labels: Record<EventStatus, string> = {
  DRAFT: 'Szkic',
  PUBLISHED: 'Opublikowane',
  HIDDEN: 'Ukryte',
  CANCELLED: 'Anulowane',
  ARCHIVED: 'Zarchiwizowane',
};

export function EventStatusBadge({ status }: EventStatusBadgeProps) {
  return <span className={`badge event-status-${status}`}>{labels[status]}</span>;
}
