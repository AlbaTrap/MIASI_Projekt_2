import { AccountStatus } from '../../types/account';

type AccountStatusBadgeProps = {
  status: AccountStatus;
};

export const accountStatusLabels: Record<AccountStatus, string> = {
  ACTIVE: 'Aktywne',
  BLOCKED: 'Zablokowane',
  DELETED: 'Usunięte',
  PENDING_CONFIRMATION: 'Oczekuje na potwierdzenie',
};

export function AccountStatusBadge({ status }: AccountStatusBadgeProps) {
  return <span className={`badge account-status-${status}`}>{accountStatusLabels[status]}</span>;
}
