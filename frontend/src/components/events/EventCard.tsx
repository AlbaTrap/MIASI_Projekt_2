import { Link } from 'react-router-dom';

import { getEventLifecyclePresentation } from '../../features/events/eventLifecycle';
import { SearchResult } from '../../types/events';
import { formatDateTime } from '../../utils/date';
import { EventCategoryVisual } from './EventCategoryVisual';

type EventCardProps = {
  event: SearchResult;
  context?: 'catalog' | 'saved';
};

const formatDateBadge = (value: string) => {
  const date = new Date(value);

  return {
    day: new Intl.DateTimeFormat('pl-PL', { day: '2-digit' }).format(date),
    month: new Intl.DateTimeFormat('pl-PL', { month: 'short' }).format(date).replace('.', ''),
  };
};

export function EventCard({ context = 'catalog', event }: EventCardProps) {
  const dateBadge = formatDateBadge(event.startsAt);
  const lifecycle = getEventLifecyclePresentation(event.status);
  const showLifecycle = context === 'saved' && event.status && event.status !== 'PUBLISHED';

  return (
    <article className={`event-card ${showLifecycle ? lifecycle.className : ''}`}>
      <EventCategoryVisual
        category={event.category}
        lifecycleTone={showLifecycle ? lifecycle.visualTone : 'normal'}
      />

      <div className="event-card-content">
        <div className="event-card-topline">
          <span className="category-pill">{event.category}</span>
          <time className="event-date-badge" dateTime={event.startsAt}>
            <span>{dateBadge.day}</span>
            <span>{dateBadge.month}</span>
          </time>
        </div>

        {showLifecycle ? <strong className="event-lifecycle-label">{lifecycle.label}</strong> : null}
        <h2>{event.name}</h2>
        {showLifecycle && lifecycle.notice ? <p className="event-lifecycle-notice">{lifecycle.notice}</p> : null}
        {showLifecycle && event.status === 'CANCELLED' && event.cancelReason ? (
          <p className="event-lifecycle-notice">Powód anulowania: {event.cancelReason}</p>
        ) : null}
        <p>{event.shortDescription}</p>
        <p className="event-card-location">{event.location}</p>

        <div className="event-card-footer">
          <time dateTime={event.startsAt}>{formatDateTime(event.startsAt)}</time>
          <Link className="text-link" to={`/events/${event.eventId}`}>
            Zobacz szczegóły
          </Link>
        </div>
      </div>
    </article>
  );
}
