
import { axiosInstanceSupport } from '@/utils/axiosInstance'

interface ISupport {
	negotiationsAndAnalysis?: boolean;
	preliminaryContractConclusion?: boolean;
	businessDueDiligence?: boolean;
	financialAnalysis?: boolean;
	financialPlanDevelopment?: boolean;
	mainContractConclusion?: boolean;
	postDealSupport?: boolean;
}

export const postSettings = async ({listingId}: {listingId : number} , support : ISupport) => {
  try {
    const response = await axiosInstanceSupport.post(
      `/${listingId}`, support

			,
			{  
				headers: {
        	Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
      },} 
    );
    return response.data;
  } catch (error) {
    console.error('Ошибка при отправке комментария:', error);
    throw error;
  }
};