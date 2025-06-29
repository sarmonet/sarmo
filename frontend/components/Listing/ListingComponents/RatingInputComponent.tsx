import { postRating } from '@/services/Rating'
import { colors } from '@/utils'
import { useState } from 'react'
import { MdStar, MdStarBorder } from 'react-icons/md'
interface RatingInputProps {
  listingId: number;
  userId: number;
  onRatingChange: (newRating: number) => void;
}

export const RatingInput: React.FC<RatingInputProps> = ({ listingId, onRatingChange }) => {
  
  const [showButton, setShowButton] = useState(false);
  const [userRating, setUserRating] = useState<number>(0);
  const MAX_STARS = 5;

  const handleStarClick = (value: number) => {
    setUserRating(value);
    setShowButton(true);
  };

  const handleSendRating = async () => {
    if (userRating > 0) {
      try {
        const token = localStorage.getItem("accessToken");
        const response = await postRating({ listingId, token, value: userRating });
        if (response) {
          onRatingChange(userRating);
          setShowButton(false);
        }
      } catch (error) {
        console.error('Ошибка при отправке рейтинга:', error);
      }
    }
  };

  const renderStars = () => {
  return Array.from({ length: MAX_STARS }, (_, i) => {
    const starValue = i + 1;
    return (
      <button
        key={starValue}
        onClick={() => handleStarClick(starValue)}
        style={{ background: 'none', border: 'none', padding: 0, cursor: 'pointer' }}
      >
        {starValue <= userRating ? (
          <MdStar color={colors.btnMainColor} size={24} />
        ) : (
          <MdStarBorder color={colors.borderColor} size={24} />
        )}
      </button>
    );
  });
};


  return(
    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
      <div style={{ display: 'flex', gap: '4px' }}>{renderStars()}</div>
      {showButton && (
        <button
          onClick={handleSendRating}
          style={{
            background: colors.btnMainColor,
            color: '#fcfcfc',
            padding: '8px 16px',
            border: 'none',
            borderRadius: '8px',
            cursor: 'pointer',
          }}
        >
          Отправить
        </button>
      )}
    </div>
  )};