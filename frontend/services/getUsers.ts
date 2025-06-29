import { axiosInstanceDefault, axiosInstanceDoc, axiosInstanceUsers } from '@/utils/axiosInstance'

interface IUserInfProps {
  email?: string;
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  country?: string;
  city?: string;
  birthDate?: string;
  fullAddress?: string;
}
export enum InvestmentGoal {
  CAPITAL_PRESERVATION =  'CAPITAL_PRESERVATION',
  STABLE_INCOME          = 'STABLE_INCOME',
  FAST_GROWTH            = 'FAST_GROWTH',
  LONG_TERM_DEVELOPMENT  = 'LONG_TERM_DEVELOPMENT',
}
export const InvestmentGoalLabels: Record<InvestmentGoal, string> = {
  [InvestmentGoal.CAPITAL_PRESERVATION]:  'Сохранение капитала',
  [InvestmentGoal.STABLE_INCOME]:         'Получение стабильного дохода',
  [InvestmentGoal.FAST_GROWTH]:           'Быстрый рост вложений (высокий риск)',
  [InvestmentGoal.LONG_TERM_DEVELOPMENT]: 'Долгосрочное развитие бизнеса',
};


export enum BudgetRange {
  UNDER_50K        = 'UNDER_50K',
  FROM_50K_TO_100K = 'FROM_50K_TO_100K',
  FROM_100K_TO_500K= 'FROM_100K_TO_500K',
  FROM_500K_TO_1M  = 'FROM_500K_TO_1M',
}
export const BudgetRangeLabels: Record<BudgetRange, string> = {
  [BudgetRange.UNDER_50K]:        'До 50 000 $',
  [BudgetRange.FROM_50K_TO_100K]: 'От 50 000 $ до 100 000 $',
  [BudgetRange.FROM_100K_TO_500K]:'От 100 000 $ до 500 000 $',
  [BudgetRange.FROM_500K_TO_1M]:  'От 500 000 $ до 1 000 000 $',
};

export enum InvestmentCategory {
  FRANCHISES             = 'FRANCHISES',
  IT_STARTUPS            = 'IT_STARTUPS',
  READY_MADE_BUSINESS    = 'READY_MADE_BUSINESS',
  INVESTMENT_PROJECTS    = 'INVESTMENT_PROJECTS',
  COMMERCIAL_REAL_ESTATE = 'COMMERCIAL_REAL_ESTATE',
  BUSINESS_PLANS         = 'BUSINESS_PLANS',
  BUSINESS_IDEAS         = 'BUSINESS_IDEAS',
}
export const InvestmentCategoryLabels: Record<InvestmentCategory, string> = {
  [InvestmentCategory.FRANCHISES]:             'Франшизы',
  [InvestmentCategory.IT_STARTUPS]:            'IT-стартапы',
  [InvestmentCategory.READY_MADE_BUSINESS]:    'Готовый бизнес',
  [InvestmentCategory.INVESTMENT_PROJECTS]:    'Инвест-проекты',
  [InvestmentCategory.COMMERCIAL_REAL_ESTATE]: 'Коммерческая недвижимость',
  [InvestmentCategory.BUSINESS_PLANS]:         'Бизнес-планы',
  [InvestmentCategory.BUSINESS_IDEAS]:         'Бизнес-идеи',
};
export enum BusinessSector {
  FOOD_SERVICE              = 'FOOD_SERVICE',
  AUTOMOTIVE                = 'AUTOMOTIVE',
  IT_PROJECTS               = 'IT_PROJECTS',
  REAL_ESTATE_CONSTRUCTION  = 'REAL_ESTATE_CONSTRUCTION',
  MANUFACTURING_INDUSTRY    = 'MANUFACTURING_INDUSTRY',
  AGRICULTURE_ECOLOGY       = 'AGRICULTURE_ECOLOGY',
  OTHER                     = 'OTHER',
}
export const BusinessSectorLabels: Record<BusinessSector, string> = {
  [BusinessSector.FOOD_SERVICE]:             'Общественное питание (рестораны, кафе)',
  [BusinessSector.AUTOMOTIVE]:               'Автомобильный бизнес (автомойки, сервисы)',
  [BusinessSector.IT_PROJECTS]:              'IT-проекты',
  [BusinessSector.REAL_ESTATE_CONSTRUCTION]: 'Недвижимость и строительство',
  [BusinessSector.MANUFACTURING_INDUSTRY]:   'Производство и промышленность',
  [BusinessSector.AGRICULTURE_ECOLOGY]:      'Сельское хозяйство и экология',
  [BusinessSector.OTHER]:                    'Другие',
};
export enum TimeCommitment {
  FULL_TIME = 'FULL_TIME',
  PART_TIME = 'PART_TIME',
}
export const TimeCommitmentLabels: Record<TimeCommitment, string> = {
  [TimeCommitment.FULL_TIME]: 'Полный рабочий день (активное участие)',
  [TimeCommitment.PART_TIME]: 'Несколько часов в неделю (пассивное участие)',
};
export interface IInvestorProfile {
  investmentGoals: InvestmentGoal[];
  businessExperience: boolean;
  experiencePeriod: string;
  experienceSphere: string;
  budget: BudgetRange;
  preferredInvestmentCategories: InvestmentCategory[];
  preferredBusinessSectors: BusinessSector[];
  otherSector: string;
  interestsAndHobbies: string;
  timeCommitment: TimeCommitment;
  aboutMe: string;
}
export const getUser = async () => {
  try {
    const { data } = await axiosInstanceUsers.get(`/me`, {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
        "Content-Type": "application/json",
      },
      withCredentials: true,
    });


    if (data) {
      return data;
    } else {
      throw new Error("Данные пользователя отсутствуют в ответе сервера.");
    }
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
  } catch (error: any) {
    if(error.response.status === 404) {
      return undefined;
    }else if(error.response.status === 500){
      return undefined;
    }
    console.error("❌ Ошибка при получении данных пользователя:", error);
    throw error;
  }
};
export const getUserById = async (id: number) => {
  try {
    const { data } = await axiosInstanceUsers.get(`user/${id}`);

   
    if (Array.isArray(data) && data.length > 0) {
      return data; 
    } else {
      throw new Error("Данные пользователя отсутствуют в ответе сервера.");
    }
  } catch (error) {
    console.error("❌ Ошибка при получении данных пользователя:", error);
    throw error;
  }
};
export const getUsers = async () => {
  try {
    const { data } = await axiosInstanceDefault.get(`/user/me`);

   
    if (Array.isArray(data) && data.length > 0) {
      return data; 
    } else {
      throw new Error("Данные пользователя отсутствуют в ответе сервера.");
    }
  } catch (error) {
    console.error("❌ Ошибка при получении данных пользователя:", error);
    throw error;
  }
};


export const postUserDoc = async (documentUrl: string) => {
  try {
    const response = await axiosInstanceDoc.post(
      `/user/me/documents`,
      documentUrl,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        },
      }
    );
    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при загрузке документа пользователя:", error);
    throw error;
  }
};
export const putUserImg = async ( profilePictureUrl: string ) => {
  try {
    const response = await axiosInstanceUsers.put(
      `/me/profile-picture`, profilePictureUrl ,
      {
        headers: {
          'Content-Type': 'text/plain',
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        },
      }
     
    );
    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при обновлении аватара пользователя:", error);
    throw error;
  }
};

export const putUserInfo = async (profileProp: IUserInfProps) => {
  try {
    const response = await axiosInstanceUsers.put(
      `/me`,
      profileProp , 
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem('accessToken')}`,
        },
      }
     
    );
    return response.data;
  } catch (error) {
    console.error("❌ Ошибка при обновлении аватара пользователя:", error);
    throw error;
  }
};
export const getInvestInfo = async (): Promise<IInvestorProfile> => {
  try {
    const token = localStorage.getItem('accessToken')
    const response = await axiosInstanceUsers.get<IInvestorProfile>(
      '/investor-form/me',
      {
        headers: {
          Authorization: token ? `Bearer ${token}` : '',
        },
      }
    )
    return response.data
  } catch (error) {
    console.error('❌ Ошибка при загрузке инвестиционного профиля:', error)
    throw error
  }
}

export const postInvestInfo = async (
  investProp: IInvestorProfile
): Promise<IInvestorProfile> => {
  try {
    const token = localStorage.getItem('accessToken')
    const response = await axiosInstanceUsers.post<IInvestorProfile>(
      '/investor-form/me',
      investProp,
      {
        headers: {
          Authorization: token ? `Bearer ${token}` : '',
        },
      }
    )
    return response.data
  } catch (error) {
    console.error('❌ Ошибка при сохранении инвестиционного профиля:', error)
    throw error
  }
}
