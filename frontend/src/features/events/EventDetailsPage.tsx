import { type ReactNode, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

import { NotificationChannel } from '../../api/backendTypes';
import { eventsApi } from '../../api/eventsApi';
import { favoritesApi } from '../../api/favoritesApi';
import { notificationsApi } from '../../api/notificationsApi';
import { EventCategoryVisual } from '../../components/events/EventCategoryVisual';
import { EventStatusBadge } from '../../components/events/EventStatusBadge';
import { AppButton } from '../../components/ui/AppButton';
import { LoadingState } from '../../components/ui/LoadingState';
import { mapCatalogEventDto } from '../../mappers/eventMapper';
import { useAuth } from '../../state/authStore';
import { CatalogEvent } from '../../types/events';
import { formatDateTime } from '../../utils/date';
import { formatSourceTypeLabel } from '../../utils/eventLabels';
import { getEventLifecyclePresentation, isUnavailableForUser } from './eventLifecycle';

export function EventDetailsPage() {
  const { eventId } = useParams();
  const { isAuthenticated, session } = useAuth();
  const [event, setEvent] = useState<CatalogEvent | null>(null);
  const [isFavorite, setIsFavorite] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isFavoriteSaving, setIsFavoriteSaving] = useState(false);
  const [isNotificationSending, setIsNotificationSending] = useState(false);
  const [error, setError] = useState('');
  const [favoriteMessage, setFavoriteMessage] = useState('');
  const [notificationMessage, setNotificationMessage] = useState('');
  const [notificationChannel, setNotificationChannel] = useState<NotificationChannel>('EMAIL');

  useEffect(() => {
    let active = true;

    const loadEvent = async () => {
      if (!eventId) {
        setError('Brak identyfikatora wydarzenia.');
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError('');
        const data = await eventsApi.getEventDetails(eventId);
        const mappedEvent = mapCatalogEventDto(data);

        if (isUnavailableForUser(mappedEvent.status)) {
          throw new Error('To wydarzenie nie jest dostępne.');
        }

        if (active) {
          setEvent(mappedEvent);
        }
      } catch (caughtError) {
        if (active) {
          setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się pobrać wydarzenia.');
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    };

    void loadEvent();

    return () => {
      active = false;
    };
  }, [eventId]);

  useEffect(() => {
    let active = true;

    const loadFavoriteState = async () => {
      if (!session?.accessToken || !eventId) {
        setIsFavorite(false);
        return;
      }

      try {
        const favorites = await favoritesApi.getFavorites(session.accessToken);

        if (active) {
          setIsFavorite(favorites.some((favorite) => favorite.eventId === eventId));
        }
      } catch {
        if (active) {
          setIsFavorite(false);
        }
      }
    };

    void loadFavoriteState();

    return () => {
      active = false;
    };
  }, [eventId, session?.accessToken]);

  const handleFavoriteToggle = async () => {
    if (!event || !session?.accessToken) {
      return;
    }

    try {
      setIsFavoriteSaving(true);
      setFavoriteMessage('');
      setNotificationMessage('');

      if (isFavorite) {
        await favoritesApi.removeFavorite(session.accessToken, event.id);
        setIsFavorite(false);
        setFavoriteMessage('Usunięto wydarzenie z ulubionych.');
      } else {
        await favoritesApi.addFavorite(session.accessToken, event.id);
        setIsFavorite(true);
        setFavoriteMessage('Dodano wydarzenie do ulubionych.');
      }
    } catch (caughtError) {
      setFavoriteMessage(
        caughtError instanceof Error ? caughtError.message : 'Nie udało się zaktualizować ulubionych.',
      );
    } finally {
      setIsFavoriteSaving(false);
    }
  };

  const handleSendNotification = async () => {
    if (!event || !session?.accessToken || !isFavorite || event.status !== 'PUBLISHED') {
      return;
    }

    try {
      setIsNotificationSending(true);
      setNotificationMessage('');
      await notificationsApi.sendEventNotification(
        session.accessToken,
        event.id,
        notificationChannel,
      );
      setNotificationMessage('Powiadomienie zostało utworzone.');
    } catch (caughtError) {
      setNotificationMessage(
        caughtError instanceof Error ? caughtError.message : 'Nie udało się utworzyć powiadomienia.',
      );
    } finally {
      setIsNotificationSending(false);
    }
  };

  if (isLoading) {
    return (
      <main className="page-shell narrow-page">
        <LoadingState />
      </main>
    );
  }

  if (error || !event) {
    return (
      <main className="page-shell narrow-page">
        <section className="panel">
          <h1>Wydarzenie niedostępne</h1>
          <p>{error || 'Nie znaleziono wydarzenia.'}</p>
          <Link to="/">
            <AppButton>Wróć do wydarzeń</AppButton>
          </Link>
        </section>
      </main>
    );
  }

  const lifecycle = getEventLifecyclePresentation(event.status);
  const showLifecycle = event.status === 'CANCELLED' || event.status === 'ARCHIVED';
  const canAddFavorite = event.status === 'PUBLISHED';
  const canSendNotification = isFavorite && event.status === 'PUBLISHED';
  const wasUpdated = new Date(event.updatedAt).getTime() > new Date(event.createdAt).getTime();
  const locationLines = getUniqueLocationLines([event.placeName, event.address, event.city]);

  return (
    <main className="page-shell details-page">
      <section className={`panel details-hero ${showLifecycle ? lifecycle.className : ''}`}>
        <EventCategoryVisual
          category={event.category}
          lifecycleTone={showLifecycle ? lifecycle.visualTone : 'normal'}
          size="hero"
        />
        <div className="details-hero-copy">
          <div className="details-status-row">
            <EventStatusBadge status={event.status} />
            <span className="category-pill">{event.category}</span>
          </div>
          {showLifecycle ? <strong className="event-lifecycle-label">{lifecycle.label}</strong> : null}
          <h1>{event.title}</h1>
          {showLifecycle && lifecycle.notice ? <p className="event-lifecycle-notice">{lifecycle.notice}</p> : null}
          {event.status === 'CANCELLED' && event.cancelReason ? (
            <p className="event-lifecycle-notice">Powód anulowania: {event.cancelReason}</p>
          ) : null}
          {wasUpdated ? <p className="hint">Zaktualizowano: {formatDateTime(event.updatedAt)}</p> : null}
          <div className="button-column details-actions">
            {isAuthenticated ? (
              <>
                <AppButton
                  disabled={isFavoriteSaving || (!isFavorite && !canAddFavorite)}
                  onClick={handleFavoriteToggle}
                  variant={isFavorite ? 'secondary' : 'primary'}
                >
                  {isFavorite ? 'Usuń z ulubionych' : 'Dodaj do ulubionych'}
                </AppButton>
                {canSendNotification ? (
                  <>
                    <label className="select-field" htmlFor="notification-channel">
                      <span>Kanał powiadomienia</span>
                      <select
                        id="notification-channel"
                        onChange={(event) => setNotificationChannel(event.target.value as NotificationChannel)}
                        value={notificationChannel}
                      >
                        <option value="EMAIL">E-mail</option>
                        <option value="SMS">SMS</option>
                      </select>
                    </label>
                    <AppButton
                      disabled={isNotificationSending}
                      onClick={handleSendNotification}
                      variant="secondary"
                    >
                      Powiadom mnie
                    </AppButton>
                  </>
                ) : (
                  <p className="hint">
                    {event.status === 'PUBLISHED'
                      ? 'Dodaj wydarzenie do ulubionych, aby otrzymać powiadomienie.'
                      : 'Powiadomienia są dostępne tylko dla opublikowanych wydarzeń.'}
                  </p>
                )}
              </>
            ) : (
              <Link to="/login">
                <AppButton variant="secondary">Zaloguj się, żeby dodać do ulubionych</AppButton>
              </Link>
            )}
            {favoriteMessage ? <p className="hint">{favoriteMessage}</p> : null}
            {notificationMessage ? <p className="hint">{notificationMessage}</p> : null}
          </div>
        </div>
      </section>

      <section className="details-grid">
        <InfoCard title="Termin">
          <p>Start: {formatDateTime(event.startDate)}</p>
          {event.endDate ? <p>Koniec: {formatDateTime(event.endDate)}</p> : null}
        </InfoCard>
        <InfoCard title="Miejsce">
          {locationLines.length > 0 ? (
            locationLines.map((line) => <p key={line}>{line}</p>)
          ) : (
            <p>Brak danych miejsca.</p>
          )}
        </InfoCard>
        <InfoCard title="Kategoria">
          <p>{event.category}</p>
        </InfoCard>
        <InfoCard className="details-description-card" title="Opis wydarzenia">
          <p>{event.description || 'Brak opisu wydarzenia.'}</p>
        </InfoCard>
        <InfoCard title="Organizator">
          <p>{event.organizerName || 'Brak danych organizatora'}</p>
        </InfoCard>
        <InfoCard title="Źródło">
          <p>{formatSourceTypeLabel(event.sourceType)}</p>
        </InfoCard>
      </section>

      <Link className="details-back-link" to="/">
        <AppButton variant="secondary">Wróć do wydarzeń</AppButton>
      </Link>
    </main>
  );
}

type InfoCardProps = {
  className?: string;
  title: string;
  children: ReactNode;
};

function InfoCard({ className = '', title, children }: InfoCardProps) {
  return (
    <article className={`panel info-card ${className}`.trim()}>
      <h2>{title}</h2>
      {children}
    </article>
  );
}

function getUniqueLocationLines(values: string[]) {
  const normalizedValues = new Set<string>();
  const lines: string[] = [];

  values.forEach((value) => {
    const trimmedValue = value.trim();
    const normalizedValue = trimmedValue.toLowerCase();

    if (trimmedValue && !normalizedValues.has(normalizedValue)) {
      normalizedValues.add(normalizedValue);
      lines.push(trimmedValue);
    }
  });

  return lines;
}
