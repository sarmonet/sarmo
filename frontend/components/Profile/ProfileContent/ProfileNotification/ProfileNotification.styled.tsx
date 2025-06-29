import { colors } from '@/utils'
import styled from '@emotion/styled'

export const ProfileNotificationItems = styled.div`
  display: flex;
  flex-direction: column;
  gap: 16px;
`;

export const ProfileNotificationItem = styled.div`
  position: relative;
  display: flex;
  flex-direction: column;
 	gap: 18px;
  background-color: ${colors.profileBgColor};
  color: ${colors.mainTextColor};
  padding: 16px;
  border-radius: 12px;
  border: 1px solid ${colors.borderColor};
  border-radius: 18px;
`;

export const ProfileNotificationHeader = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const CategoryTitle = styled.div`
  font-size: 16px;
  font-weight: bold;
`;

export const NewBadge = styled.div`
  background-color: ${colors.btnMainColor};
  color: white;
  border-radius: 12px;
  padding: 2px 8px;
  font-size: 12px;
`;

export const FiltersTextWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  
	
  span {
    background: ${colors.mainWhiteTextColor};
    padding: 4px 12px;
    border-radius: 18px;
    border: 1px solid ${colors.borderColor};
  }
`;


export const ProfileNotificationSwitch = styled.div`
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
	

  select {
    cursor: pointer;
    color: ${colors.mainTextColor};
  }
  option {
    color: ${colors.mainTextColor};
}
	}
`;
export const Delete = styled.button`
  position: absolute;
  top: -5%;
  right: 97%;
  padding: 5px;
  background-color: ${colors.mainWhiteTextColor};
  border: 1px solid ${colors.borderColor};
  border-radius: 50%;
	
	span {
		cursor: pointer;
	}
    @media (max-width: 768px) {
      right: 95%;
    }
    @media (max-width: 480px) {
      top: -4%;
      right: 93%;
    }
`;
export const Save = styled.button`
	color: ${colors.mainWhiteTextColor};
  padding: 4px 12px;
  border-radius: 18px;
  font-size: 18px;
  background-color: ${colors.btnSecondColor};
`;
