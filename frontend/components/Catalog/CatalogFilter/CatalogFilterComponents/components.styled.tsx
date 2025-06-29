import { colors } from '@/utils'
import styled from "@emotion/styled"



export const FilterWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 15px;
  width: 100%;
	border-top: 1px solid ${colors.borderColor};
	padding-top: 20px;
	margin-top: 20px;	
  @media (max-width: 1280px) {
  span{
    max-width: 60%;    
  }
  @media (max-width: 1024px) {
  span{
    color: ${colors.subtitleTextColor};
  }
   flex-direction: row;
   align-items: center;
  }
  
`;
export const SelectWrapper = styled.div`
  position: relative;
  background: #fcfcfc;
  padding: 10px 10px 10px 20px;
  border-radius: 180px;
  cursor: pointer;
	width: 90%;
`;
export const FilterItem = styled.div`
  display: flex;
  align-items:center;
  justify-content: space-between;
  font-size: 16px;  
    @media (max-width: 1024px) {
      font-size: clamp(14px, 3vw, 18px);
    }
`;

export const ErrorText = styled.div`
  color: red;
  font-size: 14px;
  margin-top: 5px;
`;
export const Dropdown = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  width: 100%;
  background: #fcfcfc;
  border: 1px solid ${colors.borderColor};
  border-radius: 8px;
  margin-top: 5px;
  max-height: 150px;
  overflow-y: auto;
  z-index: 10;

  div {
    padding: 10px;
    cursor: pointer;
    transition: background 0.2s;

    &:hover {
      background: #f0f0f0;
    }
  }
`;

export const FilterButtons = styled.div`
  display: flex;
  justify-content: space-between;
  margin-top: 20px;

  button {
    padding: 10px 20px;
    border: none;
    border-radius: 5px;
    cursor: pointer;
    font-size: 16px;

    &:first-of-type {
      background: green;
      color: #fcfcfc;
    }

    &:last-of-type {
      background: #e0e0e0;
      color: #050505;
    }

    &:hover {
      opacity: 0.8;
    }
  }

   @media (max-width: 1024px) {
      margin-top: 50px;
    }
`;