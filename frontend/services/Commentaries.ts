import { ICommentaries } from '@/components/Listing/ListingComponents/ListingComentaries/Commentaries.interface'
import { axiosInstanceCommentaries } from '@/utils/axiosInstance'
interface GetCommentParams {
  listingId: number;
}
interface DelCommentParams {
  commentId: number;
}

export interface PostCommentParams {
  listingId: number;
  userId?: number;
  content: string;
	parentCommentId?: number;
}

export const postComment = async (postCommentParams: PostCommentParams): Promise<ICommentaries> => {
  try {
    const response = await axiosInstanceCommentaries.post<ICommentaries>(
      `/comment`, 
      { 
        listingId: postCommentParams.listingId, 
        content: postCommentParams.content, 
        parentCommentId: postCommentParams.parentCommentId 
      },
      {
        headers: { 
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
         "Content-Type": "application/json"
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('Ошибка при отправке комментария:', error);
    throw error;
  }
};


export const getComment = async (
  getCommentParams: GetCommentParams
): Promise<ICommentaries[] | undefined> => {
  try {
    const response = await axiosInstanceCommentaries.get<ICommentaries[]>(
      `/comment/listing/${getCommentParams.listingId}`
    );
    return response.data;
  } catch (error) {
    console.error('Ошибка при получении комментариев:', error);
    return undefined;
  }
};
export const delComment = async (
  delCommentParams: DelCommentParams
) => {
  try {
    const response = await axiosInstanceCommentaries.delete(
      `/comment/${delCommentParams.commentId}`,
      {
        headers: { 
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        }
      }
    );
    return response.data;
  } catch (error) {
    console.error('Ошибка при получении комментариев:', error);
    return undefined;
  }
};