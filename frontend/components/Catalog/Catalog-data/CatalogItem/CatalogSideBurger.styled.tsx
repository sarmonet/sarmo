import { colors } from '@/utils'
import styled from '@emotion/styled'
export const CatalogSideBurgerWrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  
  background-color: #fff;
  z-index: 1001;
  transform: translateX(-100%);
  transition: transform 0.3s ease-in-out;
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  overflow-x: hidden;
  &.open {
    transform: translateX(0);
  }
`;

export const CatalogSideBurgerHeader = styled.div`
  display: flex;
  align-items: center;
  gap: 116px;
  padding: 16px;
    margin: 0 auto;

  border-bottom: 1px solid ${colors.borderColor};
  button {
    background: none;
    border: none;
    cursor: pointer;
    padding: 8px;
  }

  span {
    font-size: 28px;
    margin: 0 20px;
    font-weight: 500;
  }
    @media (max-width: 768px) {
    gap: 36px;
    }
`;

export const CatalogSideBurgerList = styled.ul`
  list-style: none;
  padding: 0;
  width: 90%;
  flex-grow: 1;
  overflow-y: hidden;
  margin: 0 auto;
  overflow-y: auto;
  li {
    padding: 16px;
    border-bottom: 1px solid ${colors.borderColor};
    display: flex;
    justify-content: space-between;
    align-items: center;
    cursor: pointer;

    svg {
      color: ${colors.SecondGreyTextColor};
    }
  }
`;

export const CatalogSideBurgerSubList = styled.div`
  padding: 16px;
  overflow-y: auto;
`;

export const CatalogSideBurgerFooter = styled.div`
  border-top: 1px solid ${colors.borderColor};
  padding: 16px;
  display: flex;
  gap: 16px;

`;

export const CatalogSideBurgerButton = styled.button`
  flex-grow: 1;
  padding: 12px 16px;
  border: 1px solid ${colors.borderColor};
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  

`;