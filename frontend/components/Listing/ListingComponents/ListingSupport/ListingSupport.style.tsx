import { colors } from '@/utils'
import styled from '@emotion/styled'
export const ListingSupportWrapper = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  z-index: 1000;
  background-color: rgba(0, 0, 0, 0.5); 
  display: flex;
  align-items: center;
  justify-content: center;
`;
export const ClosePop = styled.button`
	position: absolute;
	right: 30px;3
	top: 30px;
	background: none;
	border: none;
	font-size: 18px;
	cursor: pointer;
	color: ${colors.categoryLink};
`;
export const Submit = styled.button`
	background-color: ${colors.btnMainColor};
	color: ${colors.mainWhiteTextColor};
	font-size: 18px;
	padding: 15px 0;
	width: 100%;
	font-weight: 500;
	line-height: 26px;
	border-radius: 180px;
	margin-top: 20px;
`;

export const ListingSupport = styled.div`
  position: relative;
  width: 600px; 
  height: 60%;
  overflow-y: auto;
  max-width: 90%; 
  padding: 40px;
  background-color: ${colors.mainWhiteTextColor};
  border-radius: 10px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  overflow-y: auto;
  h2 {
    font-size: 32px;
    font-weight: 500;
    line-height: 42px;
    margin-bottom: 20px;
  }

  ul {
    display: flex;
    flex-direction: column;
    gap: 10px; 
  }

  li {
    font-size: 22px;
    font-weight: 400;
    color: ${colors.categoryLink};
  }

  input {
		transform: translateY(-13px);
		width: 20px;
		height: 20px;
  }

  @media (max-width: 768px) {
   h2{
      font-size: 27px;
    }
    }
`;
export const ListingItem = styled.li`
	display: flex;
  flex-direction: column;
  column-gap: 15px;
  ol{
    display: flex;
    flex-direction: column;
    list-style-type: initial;
    
    li{
      font-size: 14px;
      color: ${colors.SecondGreyTextColor};
      line-height: 22px;
      font-weight: 700;
      margin-left: 33px;
    }
  }
  span{
    font-size: 22px;
    line-height: 34px;
  }
   @media (max-width: 768px) {
   span{
      font-size: 18px;
      line-height: 28px;
    }
    }
`;