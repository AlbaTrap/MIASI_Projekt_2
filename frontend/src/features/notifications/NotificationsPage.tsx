import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';

import { InformatorDto, NotificationChannel, NotificationDto, NotificationStatus } from '../../api/backendTypes';
import { notificationsApi } from '../../api/notificationsApi';
import { AppButton } from '../../components/ui/AppButton';
import { EmptyState } from '../../components/ui/EmptyState';
import { LoadingState } from '../../components/ui/LoadingState';
import { useAuth } from '../../state/authStore';
import { formatDateTime } from '../../utils/date';

const notificationStatusLabels: Record<NotificationStatus, string> = {
  CREATED: 'Utworzone',
  SENT: 'Wysłane',
  FAILED: 'Nieudane',
};

const notificationChannelLabels: Record<NotificationChannel, string> = {
  EMAIL: 'E-mail',
  SMS: 'SMS',
};

export function NotificationsPage() {
  const { isAuthenticated, session } = useAuth();
  const [notifications, setNotifications] = useState<NotificationDto[]>([]);
  const [informators, setInformators] = useState<InformatorDto[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    let active = true;

    const loadNotifications = async () => {
      if (!session?.accessToken) {
        setIsLoading(false);
        return;
      }

      try {
        setIsLoading(true);
        setError('');
        const [notificationItems, informatorItems] = await Promise.all([
          notificationsApi.getNotifications(session.accessToken),
          notificationsApi.getInformators(session.accessToken),
        ]);

        if (active) {
          setNotifications(notificationItems);
          setInformators(informatorItems);
        }
      } catch (caughtError) {
        if (active) {
          setError(caughtError instanceof Error ? caughtError.message : 'Nie udało się pobrać powiadomień.');
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    };

    void loadNotifications();

    return () => {
      active = false;
    };
  }, [session?.accessToken]);

  if (!isAuthenticated) {
    return (
      <main className="page-shell narrow-page">
        <section className="panel">
          <h1>Powiadomienia</h1>
          <p>Zaloguj się, żeby zobaczyć powiadomienia o ulubionych wydarzeniach.</p>
          <Link to="/login">
            <AppButton>Zaloguj się</AppButton>
          </Link>
        </section>
      </main>
    );
  }

  return (
    <main className="page-shell notifications-page">
      <section className="hero-panel">
        <div className="hero-copy">
          <p className="eyebrow">Twoje przypomnienia</p>
          <h1>Powiadomienia</h1>
          <p>Tu znajdziesz powiadomienia utworzone dla wydarzeń dodanych do ulubionych.</p>
        </div>
      </section>

      {isLoading ? <LoadingState /> : null}
      {error ? <div className="notice notice-error">{error}</div> : null}

      {!isLoading && !error && notifications.length === 0 ? (
        <EmptyState title="Brak powiadomień">
          Nie masz jeszcze utworzonych powiadomień dla ulubionych wydarzeń.
        </EmptyState>
      ) : null}

      {!isLoading && !error && notifications.length > 0 ? (
        <section className="notification-list" aria-label="Lista powiadomień">
          {notifications.map((notification) => (
            <article className="panel notification-card" key={notification.id}>
              <div className="notification-card-header">
                <h2>{notification.subject || 'Powiadomienie o wydarzeniu'}</h2>
                <span className={`badge notification-status-${notification.status}`}>
                  {notificationStatusLabels[notification.status]}
                </span>
              </div>
              <p>{notification.message || 'Brak treści powiadomienia.'}</p>
              <p className="hint">Kanał: {notificationChannelLabels[notification.channel]}</p>
              <p className="hint">Utworzono: {formatDateTime(notification.createdAt)}</p>
              {notification.sentAt ? <p className="hint">Wysłano: {formatDateTime(notification.sentAt)}</p> : null}
              {notification.failureReason ? <p className="form-error">{notification.failureReason}</p> : null}
              <Link className="text-link" to={`/events/${notification.eventId}`}>
                Otwórz wydarzenie
              </Link>
            </article>
          ))}
        </section>
      ) : null}

      {!isLoading && !error && informators.length > 0 ? (
        <section className="panel">
          <h2>Informacje</h2>
          <div className="notification-list">
            {informators.map((informator) => (
              <article className="notification-note" key={informator.id}>
                <p>{informator.message}</p>
                <p className="hint">{formatDateTime(informator.createdAt)}</p>
              </article>
            ))}
          </div>
        </section>
      ) : null}
    </main>
  );
}
