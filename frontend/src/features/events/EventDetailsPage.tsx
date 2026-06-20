import { type ReactNode, useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';

import { eventsApi } from '../../api/eventsApi';
import { favoritesApi } from '../../api/favoritesApi';
import { EventCategoryVisual } from '../../components/events/EventCategoryVisual';
import { EventStatusBadge } from '../../components/events/EventStatusBadge';
import { AppButton } from '../../components/ui/AppButton';
import { LoadingState } from '../../components/ui/LoadingState';
import { mapEventDto } from '../../mappers/eventMapper';
import { useAuth } from '../../state/authStore';
import { CatalogEvent } from '../../types/events';
import { formatDateTime } from '../../utils/date';
import { getEventLifecyclePresentation, isVisibleInSavedEvents } from './eventLifecycle';

export function EventDetailsPage() {
  const { eventId } = useParams();
  const { isAuthenticated, session } = useAuth();
  const [event, setEvent] = useState<CatalogEvent | null>(null);
  const [isFavorite, setIsFavorite] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [isFavoriteSaving, setIsFavoriteSaving] = useState(false);
  const [error, setError] = useState('');
  const [favoriteMessage, setFavoriteMessage] = useState('');

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
        const mappedEvent = mapEventDto(data);

        if (mappedEvent.status === 'HIDDEN') {
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

  const lifecycle = getEventLifecyclePresentation(event.status, event.updateNotice);
  const canBeFavorited = isVisibleInSavedEvents(event);

  return (
    <main className="page-shell details-page">
      <section className={`panel details-hero ${lifecycle.className}`}>
        <EventCategoryVisual
          category={event.category}
          lifecycleTone={lifecycle.visualTone}
          size="hero"
        />
        <div className="details-hero-copy">
          <div className="details-status-row">
            <EventStatusBadge status={event.status} />
            <span className="category-pill">{event.category}</span>
          </div>
          {event.status === 'CANCELLED' || event.status === 'ARCHIVED' || event.status === 'UPDATED' ? (
            <strong className="event-lifecycle-label">{lifecycle.label}</strong>
          ) : null}
          <h1>{event.title}</h1>
          {lifecycle.notice ? <p className="event-lifecycle-notice">{lifecycle.notice}</p> : null}
          <p>{event.description}</p>
          <div className="button-column details-actions">
            {isAuthenticated ? (
              <AppButton
                disabled={!canBeFavorited || isFavoriteSaving}
                onClick={handleFavoriteToggle}
                variant={isFavorite ? 'secondary' : 'primary'}
              >
                {isFavorite ? 'Usuń z ulubionych' : 'Dodaj do ulubionych'}
              </AppButton>
            ) : (
              <Link to="/login">
                <AppButton variant="secondary">Zaloguj się, żeby dodać do ulubionych</AppButton>
              </Link>
            )}
            {favoriteMessage ? <p className="hint">{favoriteMessage}</p> : null}
          </div>
        </div>
      </section>

      <section className="details-grid">
        <InfoCard title="Termin">
          <p>Start: {formatDateTime(event.startDate)}</p>
          {event.endDate ? <p>Koniec: {formatDateTime(event.endDate)}</p> : null}
        </InfoCard>
        <InfoCard title="Miejsce">
          <p>{event.address || 'Brak dokładnego adresu'}</p>
          <p>{event.city}</p>
        </InfoCard>
        <InfoCard title="Kategoria">
          <p>{event.category}</p>
        </InfoCard>
      </section>

      <Link className="details-back-link" to="/">
        <AppButton variant="secondary">Wróć do wydarzeń</AppButton>
      </Link>
    </main>
  );
}

type InfoCardProps = {
  title: string;
  children: ReactNode;
};

function InfoCard({ title, children }: InfoCardProps) {
  return (
    <article className="panel info-card">
      <h2>{title}</h2>
      {children}
    </article>
  );
}
