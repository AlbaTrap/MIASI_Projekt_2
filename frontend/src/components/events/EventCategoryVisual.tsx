import { type CSSProperties } from 'react';

import { getCategoryVisual } from '../../utils/categoryVisuals';

type EventCategoryVisualProps = {
  category: string;
  lifecycleTone?: 'normal' | 'cancelled' | 'archived';
  size?: 'card' | 'hero';
};

type VisualStyle = CSSProperties & {
  '--visual-bg': string;
  '--visual-accent': string;
};

export function EventCategoryVisual({
  category,
  lifecycleTone = 'normal',
  size = 'card',
}: EventCategoryVisualProps) {
  const visual = getCategoryVisual(category);
  const style: VisualStyle = {
    '--visual-bg': visual.background,
    '--visual-accent': visual.accent,
  };

  return (
    <div
      className={`event-visual event-visual-${size} event-visual-${visual.tone} event-visual-${lifecycleTone}`}
      style={style}
    >
      <span className="event-visual-label">{visual.label}</span>
      <span className="event-visual-mark" aria-hidden="true">
        {visual.mark}
      </span>
    </div>
  );
}
