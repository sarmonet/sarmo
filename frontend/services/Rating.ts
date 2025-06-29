import { axiosInstanceCommentaries } from '@/utils/axiosInstance'
import axios from 'axios'
interface RatingParams {
  listingId: number;
  value: number;
  token: string | null;
}
export const postRating = async (ratingParams: RatingParams) => {
  try {
    const response = await axiosInstanceCommentaries.post(
      `/${ratingParams.listingId}/ratings`,
      {
        value: ratingParams.value,
      },
      {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${ratingParams.token}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error('Ошибка при добавлении рейтинга:', error.message);
      if (error.response) {
        console.error('Код состояния:', error.response.status);
        console.error('Данные ответа:', error.response.data);
      }
    } else {
      console.error('Неизвестная ошибка при добавлении рейтинга:', error);
    }
    throw error;
  }
};
