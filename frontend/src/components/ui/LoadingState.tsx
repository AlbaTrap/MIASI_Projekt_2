type LoadingStateProps = {
  label?: string;
};

export function LoadingState({ label = 'Ładowanie danych...' }: LoadingStateProps) {
  return <div className="notice notice-muted">{label}</div>;
}
