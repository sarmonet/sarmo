import { colors } from '@/utils'
import { MdStar, MdStarBorder, MdStarHalf } from "react-icons/md"

interface RatingProps {
  rating: number; 
}

export const RatingComponent: React.FC<RatingProps> = ({ rating }) => {
  const MAX_STARS = 5;
  const stars = [];

  for (let i = 1; i <= MAX_STARS; i++) {
    if (i <= rating) {
      stars.push(<MdStar key={i} color={colors.btnMainColor} size={24} />);
    } else if (i - 0.5 === rating) {
      stars.push(<MdStarHalf key={i} color={colors.btnMainColor} size={24} />);
    } else {
      stars.push(<MdStarBorder key={i} color={colors.borderColor}size={24} />);
    }
  }

  return <div style={{ display: "flex", gap: "4px" }}>{stars}</div>;
};
