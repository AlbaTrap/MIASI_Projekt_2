type EmptyStateProps = {
  title: string;
  children: string;
};

export function EmptyState({ title, children }: EmptyStateProps) {
  return (
    <div className="empty-state">
      <h2>{title}</h2>
      <p>{children}</p>
    </div>
  );
}
